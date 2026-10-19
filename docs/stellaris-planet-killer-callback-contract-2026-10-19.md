# Stellaris 原生巨像派生回调静态合同

## 目标与范围（实施前）

调用方原型运行 `20261019T081638Z` 在实际下达专用巨像命令时，引擎
`error.log` 第 52 行（17:05:52）报告缺少
`on_destroy_planet_with_EEP_PLANET_KILLER_DEVOURER_queued`。现有整包入口虽然检查了
P 语法、CSV 统计与武器键，却未检查由 `type=planet_killer` 隐式派生的原生回调。
旧 PASS 不能证明这些入口完整；先修复工具、验证、提交和推送，再恢复新 Mod 实机验收。

本次仅检查 Mod `common/component_templates/*.txt` 根级 `weapon_component_template`
中直接字面量 `type=planet_killer` 的 key，是否具备三个约定的根级 on_action 块。
在 Mod 和未被同路径文件遮盖的原版 `common/on_actions/*.txt` 查找定义。
不展开 inline_script／变量，不扩展其它武器类型、事件是否存在、事件作用域、
回调业务内容或实际引擎执行语义。检查独立于是否存在武器 CSV；没有巨像组件的历史
包报告不适用，不能因无 CSV 而跳过一个明确声明的巨像组件。

## 原生来源与失败事实

本机 Stellaris Cygnus 4.5.2（9776），EXE SHA-256
`400df27c82ddc845aa9dce79bd468d81f18f060d299cd263e3afa93aef4f7a83`。
原生 `common/on_actions/01_planet_destruction.txt` SHA-256
`6ba54afe8c56225641311aa64f3daa358097a0adbbdaad0328eb5d260822325c`，已只读核对：

| 组件 key 为 K | 必须存在的根级块 | 原版 CRACKER 行 |
| --- | --- | ---: |
| 完成 | `on_destroy_planet_with_K` | 14 |
| 排队／下令 | `on_destroy_planet_with_K_queued` | 27 |
| 出队／取消 | `on_destroy_planet_with_K_unqueued` | 33 |

文件第 10～12 行注释说明：queued 也在直接下令时触发，unqueued 包含充能中取消，
作用域与完成入口相同。CRACKER 的 unqueued、MAW 的 queued／unqueued 块为空，
因此空块是这个静态存在性合同的合法正例。本合同不要求给排队／取消入口添加收益。
原生名称已与调用方 colossus_review 线程交叉确认。

旧冻结 `prototype-rc1b` 的组件 key 为 `EEP_PLANET_KILLER_DEVOURER`，仅定义了
完成回调。保留冻结源不变；生产修复由调用方添加两个空回调，本工具不修改任何 Mod。

## 设计

复用现有只用于声明关联的词法投影，识别根级武器块、直接 key 和直接 type；
不把注释、字符串或嵌套同名字段当作声明。已有 Kaishek CLI 继续负责 P 语法／往返。
对每个明确的巨像组件，分别要求三个准确名称的根级块，缺项输出
`PLANET_KILLER_CALLBACK_MISSING`，包含组件路径、行号、key 和缺失 callback。
声明为 scalar、仅出现在注释／字符串／嵌套块中，都不满足存在性。

报告增加版本化 `planet_killer_callback_contract`、组件数量、所需／已找到回调数量和
适用性，以便调用方拒绝沿用缺少该合同的旧报告。已有 CSV 字段与无 CSV 历史包行为保留。

## 验收标准

- 三个准确空块通过；逐个删除任一入口都失败；仅有完成回调时精确报告缺两项。
- 原版回调回退合法；Mod 同路径文件遮盖原版时不能借被隐藏的原版块放行。
- 注释、字符串、嵌套或 scalar 伪定义不通过；非巨像组件不要求这些入口。
- 没有 CSV 的明确巨像组件也必须执行检查，缺入口 FAIL；没有巨像组件明确不适用。
- 运行受影响的整包正反测试和 README 要求的完整
  `py tools/run_static_acceptance.py`，明确记录进程退出码。仅提交工具任务文件并推送。
- 推送后只读检查：旧 rc1b 缺陷包因缺两个回调 FAIL，当前修复包回调合同通过；
  整包结果与本合同结果分开记录，不把静态通过冒充命令／取消实机验证。

## 实施与结果

起点为干净 `main`／`origin/main` 的 `45cc68d`，fetch 后没有远端差异或他人未提交改动。
增加回调声明投影与独立整包合同，9 项回调专项正反测试通过；完整
`py tools/run_static_acceptance.py` 返回进程退出码 0、`static acceptance: PASS`，包含
43 项整包回归（新增 9 项专项及 1 项无 CSV 集成）与 33 项 scope guard 回归，
以及 README 其它 metadata／Maven／domain／parser/fuzz／CLI 检查。未启动游戏，
未扫描外部 corpus。摘要见
[完整离线验收证据](evidence/stellaris-pk-callback-2026-10-19/static-acceptance-summary.txt)。

新增报告字段的确切名称为：

- `planet_killer_callback_contract="native_completed_queued_unqueued_v1"`；
- `planet_killer_components`；
- `planet_killer_callbacks_required`；
- `planet_killer_callbacks_resolved`；
- `planet_killer_callback_applicability`，值为 `checked` 或
  `not_applicable_no_planet_killer_components`。

目标专用巨像应为 1 组件／3 所需／3 已找到且 `checked`。调用方不能沿用没有这些
字段的旧报告。

工具实现 `bec0c47` 已推送 `origin/main` 后，使用精确游戏指纹，只读检查两包：

| 包 | 整包状态／退出码 | P | 回调所需／已找到 | 诊断 |
| --- | --- | --- | --- | --- |
| immutable prototype-rc1b | FAIL／1 | 34/34 | 3／1 | 仅缺 queued、unqueued 两项 |
| 当前生产修复包 | PASS／0 | 35/35 | 3／3 | errors=[]，113 本地化键 |

旧包精确输出两个 `PLANET_KILLER_CALLBACK_MISSING`，均指向组件定义第 1 行，
证明 P 和 CSV 通过不能代替派生回调存在性。新包空的 queued／unqueued 块满足
当前受限合同。报告分别为[旧冻结包失败](evidence/stellaris-pk-callback-2026-10-19/old-rc1b-missing-callbacks.json)
与[修复工作包静态通过](evidence/stellaris-pk-callback-2026-10-19/repaired-devourer-working-package.json)，
关键输入与工具提交见[指纹记录](evidence/stellaris-pk-callback-2026-10-19/callback-input-fingerprints.json)。
修复包 on_actions SHA-256 为
`f37870ca1050ca976cbee80312a8862a698ca4596bae55a7b9e9fdfec897db8e`。

上述是检查时工作包的静态校验通过，原型冻结目录没有被修改。最终候选必须重新冻结并
重跑新工具；下令、取消、发射与结算需简体中文实机，非中文运行时不在范围内。
本次没有操作桌面或启动游戏，也没有把回调业务内容的正确性写成已认证。
