# Stellaris 4.5.2 有限人口 scope 预检

## 触发、目标与边界

2026-10-15，外部 Mod 的包级解析曾返回 PASS，但 4.5.2 实际加载出现 `pop_amount` 的 Wrong scope。原入口只检查语法/包结构，缺少能提前识别此类已知 scope 错误的能力。本任务先记录合同，再实现可复用预检；不写 Mod 名字特判，不启动游戏、桌面或 Steam。

当前仓库 `main` 基线 `522ac2d`，工作树干净。仓库根和全仓文件查找均未发现 `AGENTS.md`；已阅读 README、开发流程与包级验收文档。工具实现完成后先在本仓库提交、推送，再恢复调用方 Mod 验收。

此检查只覆盖注册的人口数量表达式与有限 scope 转换，**不宣称完整 opcode、原生运行时、递归展开或脚本值执行认证**。未知入口、scope 切换、未注册游戏构建或覆盖了原版 alias 的 Mod 不能被当成已认证 PASS，应以明确 UNKNOWN 诊断暴露。其他 opcode 仍由现有包级边界约束。

## 4.5.2 规则依据

- 本机 `launcher-settings.json`：`rawVersion=v4.5.2`；EXE SHA-256 `400df27c82ddc845aa9dce79bd468d81f18f060d299cd263e3afa93aef4f7a83`。
- 原版 `common/scripted_triggers/00_scripted_triggers_pop.txt:49-51`：`pop_amount = { count_owned_pop_amount = { } }`，不是单个 pop group 的数量。
- 已有引擎生成 `triggers.log:4506-4511`：`count_owned_pop_amount` 支持 planet/country/ship/galactic_object/pop_faction/sector/colony，**不支持 pop_group**；`triggers.log:3034-3036` 的 `pop_group_size` 专用于 pop_group。
- 已有 `effects.log:5644-5692` 与 `triggers.log:4495-4522`：owned/species 的 every/any/random/count pop group 迭代内容进入 pop_group。`every/any/random_owned_species` 进入 species。
- 已有 `scopes.log` 定义 owner→country、species→species、planet→planet、root/this/prev/prevprev scope 链。条件/算术/变量容器不改变当前 scope，prev 按实际 scope 切换历史回退，而不是按花括号层数。

不复制外部日志/游戏文件进工具仓库。有限注册表绑定上述 4.5.2 构建；运行前确认安装版本/EXE 与 alias 的结构，防止不同版本和被覆盖 alias 偷用该注册表。

## 方案

1. 入口仍先使用 Kaishek CLI 对所有 P 文件作完整词法/语法 round-trip。新增轻量只读词法树只用于有限 scope 遍历，排除注释与字符串，保留行列与比较运算。
2. 检查 `pop_amount`/`pop_group_size` 的标量比较及 `trigger:<name>` 数值导出。支持显式 `this/owner/root/prev/prevprev...` 前缀链；scope 不明报告 `STELLARIS_SCOPE_UNKNOWN`，已知不支持 scope 报 `STELLARIS_SCOPE_MISMATCH`。不将字符串、注释、变量名字的子串当 opcode。
3. country/planet 等事件入口具有已知根 scope；独立 scripted effect/trigger 的入口多态，根 scope 为 UNKNOWN。已知 pop group 迭代仍足以识别内部直接误用。未知块不会被假定为不改变 scope；若其中有目标表达式则明确 UNKNOWN。
4. 保存有限 scope 栈。`every_owned_pop_group={prev={pop_amount>0}}` 在 country_event 中合法；`prev.trigger:pop_amount` 则按被引用的实际 scope 判断。未知 prev 层不能猜成 country。
5. 报告新增 `finite_scope_preflight`，列出绑定 profile、有限检查数量/错误/未知；UNKNOWN 进入整体错误列表而阻止总体 PASS。无目标表达式时明确 NOT_APPLICABLE，不能借此扩大语义结论。

## 验收计划

- 独立正反例：pop group 中错误比较/导出失败；正确 `pop_group_size` 通过；country/planet 中合法 `pop_amount` 不误报；引号/注释忽略；同名变量不误报；prev/prevprev 与点链正确；未知 scope/根/游戏构建/alias 覆盖明确 UNKNOWN。
- 程序测试验证正确诊断码、scope、行列和报告集成；已有包级测试保留。当前未修复外部 Mod 必须先出现目标 scope FAIL，采集其代码摘要与报告后，立即通知调用方恢复 Mod 修复，避免丢失故障基线。
- 工具自身执行受影响的 Python 正反例、包级入口集成测试与真实冻结包的 CLI。调用方明确要求此次不扩展到无关全 Maven：本次仅改 Python 包级入口，Java 核心不变，故全 Maven/全仓库静态流水线不在此次验证范围内。新 Python 测试仍接入推荐静态入口，供后续完整运行。仅提交本任务工具、测试与文档，不提交调用方 Mod 或用户其他文件。

## 其他已知缺口

调用方还报告普通 `common/script_values` 顶层标量被 `value:` 引用导致 Invalid script value，以及原生 scripted effect 展开深度/初始化问题。人口 scope 组件本身不负责这两类问题；本次包级入口另有下述独立 script value 结构规则，而 post-init 深度/初始化仍未覆盖。不得因为人口 scope 无错误就宣称这些问题已排除。

已独立查证原版 `common/script_values/00_script_values.txt` 开头说明及普通命名示例使用 `{ base = ... }` 块；调用方冻结包的普通标量定义被 `value:` 引用，并有 51 条原生 Invalid script value。增加第二项严格限定的结构规则：只检查 Mod `common/script_values` 中**被非字符串 `value:<name>` 引用的普通顶层数字标量定义**，4.5.2 注册构建下拒绝该形式，定义/引用位置均报告；`@` 常量、注释/字符串、普通 block 不误报。非数字标量不猜其语义，若被引用则 UNKNOWN；多重同名定义则 UNKNOWN，不臆测加载优先级。本规则不执行脚本值，也不宣称所有缺失/第三方命名引用均已认证。

另有原版 `common/scripted_effects/shroud_shadows_scripted_effects.txt:2698/2847` 提示 post-init pass limit 5。原生宏展开与运行期调用不同，本次不以 Python 调用图冒充 engine pass；该深度/初始化限制仍须专项静态证据与实机日志验证。工具报告的语义边界会明确保留此项，不因其他有限规则通过而消除此缺口。

## 实施与验证

实现为 `tools/stellaris_scope_guard.py` 的两个有限预检，并接入 `tools/accept_stellaris_mod.py`。scope 用有限词法树排除注释/字符串，检查完整比较键和数值导出（含点链/宏 scope 的 UNKNOWN），只对已注册人口表达式作判断。此层不检查全部外层迭代器输入对象是否存在或类型合法，也不沿任意 scripted effect 调用图推导入口；这些未注册语义继续在报告边界之外。普通命名 script value 的结构检查单独报告，不与人口 scope 混为一项。

受影响测试命令：

```powershell
python -m unittest tools.test_stellaris_scope_guard tools.test_accept_stellaris_mod -v
```

初步验证 37 项通过；推送前 fetch 发现远端已前进到 `721bc27`，包含 GUI/asset 发现与本地化字符丢失修复。保留全部上游修复并将本任务 rebase 到该最新 main，解决文档/测试末尾追加冲突后重新执行上述测试。

最终结果 **45 tests，退出码 0**：新有限层 25 项、包级集成 20 项，包括 country/planet 正例、八种 pop group 迭代负例、正确 group size、数值导出、prev/prevprev/点链、未知入口/切换/构建、alias 覆盖与 replace_path、注释/字符串排除、普通命名标量负例、block/@ 常量正例，以及最新上游包级合同。完整输出见 [测试日志](evidence/stellaris-population-scope-2026-10-15/affected-python-tests.log)，源码摘要和范围见 [验证摘要](evidence/stellaris-population-scope-2026-10-15/verification-summary.json)。新测试已接入 `run_static_acceptance.py`，此次未运行其 Java/Maven 全套。

调用方提供已失败加载的冻结 rc.1 包作为独立负例，共 90 文件；检查只读，不修改该包。逐文件 SHA-256 与稳定清单摘要见 [故障包清单](evidence/stellaris-population-scope-2026-10-15/frozen-rc1-manifest.json)，清单摘要 `ec232ede150d26a55873d6e0453a32eb84df3f1e057e6d53be12e5cbdc55449d`。

完整 CLI（预期失败）：

```powershell
python tools/accept_stellaris_mod.py `
  --mod C:/Users/Administrator/AppData/Local/xenoamess_stellaries_dev/runs/20261015T064353Z/mod/shishan-feedback-local `
  --version-file C:/workspace/xenoamess_stellaries_dev/_runtime/shishan_code/20261015-runtime/rc1.VERSION `
  --game 'C:/Program Files (x86)/Steam/steamapps/common/Stellaris' `
  --expected-exe-sha256 400df27c82ddc845aa9dce79bd468d81f18f060d299cd263e3afa93aef4f7a83 `
  --report docs/evidence/stellaris-population-scope-2026-10-15/rc1-frozen-final-failure.json
```

结果 **FAIL，退出码 1**：25/25 脚本仍由 Kaishek CLI 解析，13 DDS/268 本地化键结构检查通过，但人口预检精确拒绝 161 处 pop_group 上的 `pop_amount` 比较/导出；script value 预检拒绝 1 个被引用的普通标量定义，并记录定义和引用行列。无 UNKNOWN。结果见 [最终负例报告](evidence/stellaris-population-scope-2026-10-15/rc1-frozen-final-failure.json)；先完成的纯 scope 负例报告亦保留，首次成功拦截后已立即通知调用方可以并行修 Mod。

工具只读检查了游戏安装与冻结 Mod，未启动游戏、桌面或 Steam。此任务不提供修复后 Mod 的行为验收结论；调用方需等工具提交推送完成后，用工具检查新包并继续既定实机测试。post-init 深度/初始化仍不在本有限检查中。
