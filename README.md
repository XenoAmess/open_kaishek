# open_kaishek

`open_kaishek` 是一个独立的、可离线构建的 JVM 工具仓库：
[github.com/XenoAmess/open_kaishek](https://github.com/XenoAmess/open_kaishek)。
它从原项目 `Z:\ck3_mod_rewrite` 拆出；本仓库不再依赖父仓库的 Git 工作树，也不把父仓库或
游戏安装目录作为源码子目录提交。

它的目标不是重写 CK3，而是提供一条可以独立运行的 Paradox 脚本工具链：从逐字节保真的解析开始，经过版本与目录感知的静态验证、严格 IR 编译和白名单语义执行，最终在有限世界快照上运行“半套仿 CK3 Runtime”，并用真实 CK3 与 MCP 差分结果认证每一项可执行语义。

第一目标的 361 机制资料来自 companion 源仓/外部 corpus（见
[`docs/reference-repositories.md`](docs/reference-repositories.md)），不会随本仓库分发。
仓库内的 schema、测试夹具和 synthetic 014 只记录可复核的接口与有限离线行为。

## 已确定的技术约束

- 实现语言与运行平台：Java/JVM。
- 服务与集成框架：Quarkus。
- Parser、validator、IR 和 Runtime Kernel 必须保持纯 Java、无 Quarkus 依赖。
- Quarkus 只负责服务入口、配置、依赖装配、缓存和外部适配。
- 第一优先 profile 是 CK3 1.19.0.6 与 `mod_zhongguo_style` 的 361 机制。
- 未认证语义必须明确返回 `UNSUPPORTED`，禁止猜测、宽松吞错或静默 no-op。
- CK3 实机仍是版本绑定语义和正式发布验收的最终权威。

## 当前状态（2026-09-01）

当前提交包含可离线构建的纯 Java 基线：lossless
parser/CST（含 Phase 1 方括号列表、内联表达式角色、scope chain 与 reader directive）、profile API、CK3 1.19.0.6 profile 与受限的 Stellaris 4.4.6 静态 profile、schema validator、strict IR
合同、finite runtime 原语、差分 snapshot/trace 合同、361 domain schema，以及不启动
Quarkus 的 CLI。另有一条明确标注为 synthetic 的 014 纵向夹具走通 Parser → Validator
→ IR → VM。Phase 0/M0 的离线评审证据记录在
[`docs/phase0-verification.md`](docs/phase0-verification.md)；这里的 readiness 仍限定为
`static-ready`，不是 CK3 实机能力。

- Parser：目标 corpus 的 27 个 `.txt/.gui` 文件逐字节 round-trip 通过（2,677,440 bytes，0 diagnostics）；结构化 Phase 1 回归和 760-case property/fuzz smoke 通过。
- Validator/IR/Runtime：提供最小、fail-closed 的合同与单元测试；synthetic 014 夹具执行
  `delivered → appeal_open → closed`，完整 361 业务仍未形成闭环。
- CK3/MCP：exact-build 身份已记录，但没有 paused live artifact；`fixture-live`、`differential-certified` 与 `product-live` 均不可宣称。
- Quarkus：仅保留集成壳，依赖按路线图延后；没有复制第三方源码。

## 许可证与来源边界

- 本仓库作者代码、文档和测试夹具采用 [GNU GPL-3.0-only](LICENSE)。
- Maven 构建插件和测试框架等第三方组件继续使用各自的许可证；完整版本、SPDX 和归属
  记录见 [`THIRD_PARTY_LOCK`](THIRD_PARTY_LOCK)。GPL 标记不会覆盖这些独立依赖。
- CK3 本体、`mod_zhongguo_style`、父仓库的 `docs/`/MCP 工具和其他 companion 资料均不在
  本仓库中。需要重建目标 corpus 时，请显式传入它们在本机的路径；不要把这些文件复制进
  发布包。

## 独立仓库构建入口

要求 JDK 21（`--release 21`）和 Maven 3.6.3 或更高版本。所有命令都从本仓库根目录执行：

```powershell
mvn -o -ntp clean test
mvn -o -ntp -DskipTests package

py -m unittest discover -s kaishek-zg361-profile/tests -v
py kaishek-zg361-profile/tools/validate_domains.py
```

synthetic 014 夹具会由 Maven 测试覆盖；CLI smoke 是无框架的显式检查，需要手动运行时先
执行上面的构建，再运行：

```powershell
java -ea -cp "kaishek-cli/target/classes;kaishek-cli/target/test-classes;kaishek-validator/target/classes;kaishek-ck3-11906-profile/target/classes;kaishek-stellaris-446-profile/target/classes;kaishek-zg361-profile/target/classes;kaishek-ir/target/classes;kaishek-runtime/target/classes;kaishek-diff-contract/target/classes;kaishek-profile-api/target/classes;kaishek-syntax/target/classes" com.xenoamess.kaishek.cli.KaishekCliSmokeTest
```

目标 corpus 是外部输入。例如在 Windows 上可将 `Z:\ck3_mod_rewrite\mod_zhongguo_style`
（或其独立副本）传给 parser smoke 的 `--root` 参数，或传给 CLI `corpus <path>` 子命令；
该路径只是示例，不是仓库内的固定依赖。没有外部 corpus 时，仓库自身的测试仍可完整离线
运行，但不能据此宣称目标 mod 已被重新解析。

## 文档入口

- [项目立项报告](docs/project-charter.md)
- [目标架构与“半套 Runtime”边界](docs/architecture-plan.md)
- [阶段计划与里程碑](docs/roadmap.md)
- [验收与 CK3 MCP 差分策略](docs/acceptance-and-differential-testing.md)
- [参考仓库与计划复用目标](docs/reference-repositories.md)
- [ADR-0001：Java/JVM、Quarkus 与核心边界](docs/decisions/0001-java-jvm-quarkus.md)
- [ADR-0002：Phase 0（M0）开工与冻结门槛](docs/decisions/0002-phase0-start.md)
- [Phase 0 验证记录](docs/phase0-verification.md)
- [M0 正式 readiness 记录](docs/decisions/0003-m0-formal-readiness.md)
- [M1 / Phase 1 parser readiness 记录](docs/decisions/0004-m1-phase1-formal-readiness.md)
- [许可证审计与迁移记录](docs/license-audit.md)
- [开发流程与主线优先约定](docs/development-workflow.md)
- [Stellaris 4.4.6 静态 profile 边界](docs/stellaris-4.4.6-profile.md)

## Readiness 边界

`static-ready` 只表示源码、合同和离线验证可复核；它不等于 CK3 加载、Runtime
等价或 MCP 认证。任何未知 opcode、版本不匹配、缺失字段或未认证随机/调度语义都必须
显式返回 `UNSUPPORTED`。synthetic 014 仅证明有限离线夹具；后续 M3/M4/M5 仍需扩展
真实业务范围或取得 paused CK3 artifact 才能推进。

## 项目口号

> 离线穷举我们真正拥有的业务语义，实机只验证 CK3 真正拥有的引擎语义。
