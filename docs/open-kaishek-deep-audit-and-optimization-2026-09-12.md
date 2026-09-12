# open_kaishek 深度审计与有界优化（2026-09-12）

## 1. 结论

`open_kaishek` 的总体方法正确：lossless CST、profile-aware validator、strict IR、finite
runtime、native port 与 CK3 差分认证之间的边界清楚；核心模块没有反向依赖 Quarkus，未认证语义也继续以
`UNSUPPORTED`/`SKIPPED` 暴露。当前最大的工程风险不是架构方向，而是两类更具体的问题：公开合同字段的
类型校验不够精确，以及仓库级静态验收入口分散。本轮已经修复这两类问题。

产品能力仍处于有限的 `static-ready`：Parser/validator/runtime 基础可用，361 只有 synthetic 014 和若干
schema/capability slice，不能据此宣称完整 B2/Workforce 离线闭环、CK3 live 或 differential-certified。
后续资源应优先投入真实产品脚本纵向切片和差分所需观测字段，不应优先填充空的 Quarkus 外壳或扩张理论安全审计。

## 2. 审计范围与证据

- Git 基线：`c99116c1cc41d039aff2ec46278799f9521c8509`，审计开始时本地 `main`、
  `origin/main` 与 merge-base 一致，工作树为空。
- 阅读：根 README、architecture plan、roadmap、acceptance/differential、development workflow、根/子模块
  POM、CI、CLI、profile API/operator adapter、validator、IR、runtime 及主要测试。
- 代码规模：10 个有 Java 源码的模块；`kaishek-zg361-profile` 约 3,671 行主代码，Parser 约 715 行，
  `OfflinePreflight` 约 710 行，CLI 约 506 行。
- 基线验证：`mvn -o -ntp test` 全 reactor GREEN；Python domain 8/8 GREEN；domain validator 与 metadata
  gate GREEN。没有启动 CK3，没有扫描外部 mod corpus，也没有生成 live 结论。
- 审计只处理现有代码可确定复现的问题，没有派生 symlink、路径逃逸、伪造归档等理论安全工作。

## 3. 保留的正确方法

### 3.1 分层与依赖方向

Syntax、profile API、validator、IR、runtime、diff contract、游戏 profile 和 CLI 的 Maven 依赖方向与架构文档
一致。`kaishek-service-quarkus` 目前为空，但核心层没有因此依赖 CDI、HTTP 或 Quarkus 生命周期。这个边界应继续
保持。

### 3.2 Readiness 诚实性

fixture 输出会区分 `schema-only`、`synthetic-only`、`SKIPPED`、native/runtime certified 状态；CLI
preflight 也明确记录 `ck3_started=false` 与 `save_mutated=false`。这种证据分层比增加更多测试数量更有价值，
应作为未来能力接入的固定合同。

### 3.3 外部 corpus 与开源仓边界

目标 mod、游戏本体与 live artifact 没有复制进仓库，工具通过显式路径消费外部 corpus。这样既保持独立构建，
也避免把本机路径或 CK3 资产变成源码依赖。

## 4. 本轮发现并修复的问题

### P0：MCP 数字合同会接受小数并截断

**证据：** `OperatorMcpClientAdapter.requireSchema` 原先使用 `Number.intValue()`；因此
`schema_version=1.5` 会得到 `1` 并通过。`requireNonNegativeInteger` 使用 `longValue()`；因此
`payload_bytes=0.5` 会得到 `0` 并通过。两个字段的合同都要求整数，客户端却可能接受格式错误的 provider
响应。

**修复：** 新增统一的精确整数解析，使用 `BigDecimal.toBigIntegerExact()` 验证数值语义；schema 必须精确等于
1，payload bytes 必须为非负整数。整数型、`1.0` 这类数值上等于整数的 JSON number 仍兼容；小数、NaN、
Infinity 和非数字都会 fail closed。

**验证：** 新增针对 `schema_version=1.5` 与 `payload_bytes=0.5` 的回归；
`mvn -o -ntp -pl kaishek-profile-api test` 23/23 GREEN。

**兼容影响：** 没有 schema、tool、版本或请求字段变化。符合原合同、返回整数的 provider 行为不变；仅原本就违反
合同的响应会由误放行改为拒绝。因此不需要 root provider 或 native bridge 变更。

### P1：仓库级静态验收分散，增量 CLI smoke 会误判 Shade 中间产物

**证据：** 完整仓库检查分散为 Maven、Python unittest、domain validator、四个 standalone parser main
和 packaged CLI smoke。单独运行 `mvn test` 不执行被明确排除的 standalone smoke。与此同时，Windows 上的
增量 `mvn package` 实测会同时留下 canonical JAR 和 `-shaded.jar`；旧 `run_cli_smoke.py` 用宽通配符要求
恰好一个候选，因而 RED，尽管 canonical JAR 可正常运行。

**修复：** 新增 `py tools/run_static_acceptance.py`，离线优先执行仓库自有 metadata、clean Maven package、
Python domain、四个 parser smoke 和 packaged CLI smoke。`--online` 只用于 Maven 缓存未就绪的机器。CLI
smoke 改为选择 Maven install/deploy 使用的 canonical JAR，并忽略 Shade 的 `-shaded` 中间产物。README 与
development workflow 已指向这一入口，并明确它不包含 CK3 或外部 corpus。

**验证：** 先保留了中间产物误判的 RED，再完成修复并原样重跑；新入口最终 GREEN，包含 760-case parser
property/fuzz、8 个 Python domain tests、全 reactor JUnit/package 与 packaged CLI synthetic preflight。

## 5. 遗留问题与优先级

### P1：把真实产品脚本纵向切片放在下一主线

当前 361 能力大多是 schema slice、capability metadata 和 synthetic fixture。架构要求的“实际生成 `.txt`
→ parser → validator → IR → VM”仍只在有限 synthetic 014 上成立。下一主线应选择 B2 或 Workforce/endgame
中一个能产生用户可见收益的最小链路，补齐实际脚本入口、所需只读观测字段和 normalized delta；先离线闭合，
再批量做 exact-build 差分。

退出条件应是一个真实脚本链能从源文件稳定得到 IR、trace 和业务 postcondition，并且每个 native-only 字段都
明确返回 unavailable/unsupported 或有 paused artifact。不要用新增 metadata 类的数量替代这一退出条件。

### P1：建立能力目录，减少 profile/文档漂移

`kaishek-zg361-profile` 已有 31 个主类，`docs/` 中也积累了大量单能力兼容记录。继续逐项增加 Java 常量类和独立
说明，会增加版本、字段、certified 状态和来源哈希的同步成本。建议在下一批至少三个同类 capability 到来时，
引入一个版本化、机器可读的 capability catalogue，并从它验证或生成只读索引；Java 的执行逻辑仍保留类型化
实现。不要为了形式统一立即迁移现有全部条目。

### P2：在新增命令前拆分 CLI 与 OfflinePreflight

`KaishekCli` 约 506 行，测试约 476 行；`OfflinePreflight` 约 710 行。当前它们有覆盖且还能维护，立即大改收益
不足。下一次增加 CLI command 或第三类 preflight fixture family 时，应把参数解析、fixture registry、JSON
envelope 分成包内组件，并保持现有命令/退出码为黑盒回归，避免继续扩大单文件分支矩阵。

### P2：刷新顶层状态页，但不要复制伴随仓状态

README 的“当前状态”日期仍为 2026-09-01，未索引之后新增的 operator MCP、G2/capability slice 与兼容记录。
建议在 capability catalogue 或下一阶段 readiness 更新时同步刷新 README/roadmap，并继续只链接 companion 的
live evidence；不要在本仓复制一份会漂移的根项目 current-state。

### P3：Quarkus 继续延后

`kaishek-service-quarkus` 当前没有源码，构建会产生 empty JAR 警告。这与既定路线图“外壳延后”一致，不是当前
故障。Parser/validator/CLI 或真实 361 纵向切片尚未形成更完整产品价值前，不建议为消除警告而初始化 REST/CDI
层。

## 6. 建议执行顺序

1. 合入本轮精确整数合同、统一静态验收入口与 Shade 产物选择修复。
2. 选择一个真实 B2/Workforce 脚本纵向切片，先闭合离线 parser → validator → IR → VM/postcondition。
3. 缺字段时按 exact build 补最小只读 native/MCP capability，再做一次批量差分，不做永久长跑。
4. 同类 capability 再增加时建立 catalogue；新增 CLI/preflight family 时再拆分大类。
5. 有实际服务消费者后才启动 Quarkus Phase 6。

建议提交信息：`Tighten operator contracts and unify static acceptance`
