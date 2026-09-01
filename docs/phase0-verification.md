# Phase 0 / M0 验证记录

记录日期：2026-09-01（Asia/Shanghai）

本记录描述独立仓库 `open_kaishek` 静态基线的可复核结果。它只证明源码、合同和离线夹具
的一致性，不证明 CK3 会加载这些脚本，也不构成 MCP 差分认证或 `product-live` 证据。
M0 的正式静态 readiness 结论与签核清单见
[`0003-m0-formal-readiness.md`](decisions/0003-m0-formal-readiness.md)；该记录只关闭
Phase 0 的离线工程门槛，不把任何离线结果升级为 CK3 live 能力。
Phase 1/M1 parser 的边界与签核证据另见
[`0004-m1-phase1-formal-readiness.md`](decisions/0004-m1-phase1-formal-readiness.md)。

## 冻结/观测环境

- JDK：Eclipse Temurin `21.0.10+7`（`--release 21`）。
- Maven：Apache Maven `3.6.3`；离线验证使用本机缓存并显式设置
  `-Dmaven.repo.local`，项目本身不依赖开发者目录。
- 项目许可证：GPL-3.0-only，见根目录 `LICENSE`；构建期第三方许可证仍以
  [`THIRD_PARTY_LOCK`](../THIRD_PARTY_LOCK) 为准。
- 运行时依赖：核心模块没有第三方运行时依赖；JUnit 只在测试 classpath，构建插件及
  许可证见 [`THIRD_PARTY_LOCK`](../THIRD_PARTY_LOCK)。
- CK3 profile：`ck3-1.19.0.6`，EXE SHA-256
  `2D00FF3101EF70B566F2FCBAE292F09263199C80E9DC8F139B82D7D96F83DB86`。

## 可重复命令与结果

在独立仓库根目录 `Z:\workspace\open_kaishek` 执行（Windows）。下列 `$m2` 和
`$corpus` 只是可替换的本机路径；仓库不要求使用某位开发者的用户目录：

```powershell
$m2 = Join-Path $env:USERPROFILE '.m2\repository'
$env:MAVEN_OPTS = "-Duser.home=$env:USERPROFILE"
$corpus = 'Z:\ck3_mod_rewrite\mod_zhongguo_style'  # 外部、只读的本地 corpus；换成你的路径
mvn -o "-Dmaven.repo.local=$m2" clean test
java -ea -cp 'kaishek-syntax/target/classes;kaishek-syntax/target/test-classes' com.xenoamess.kaishek.syntax.ParserSelfTest
java -ea -cp 'kaishek-syntax/target/classes;kaishek-syntax/target/test-classes' com.xenoamess.kaishek.syntax.DuplicateKeyRoundTripSelfTest
java -ea -cp 'kaishek-syntax/target/classes;kaishek-syntax/target/test-classes' com.xenoamess.kaishek.syntax.ParserPropertyFuzzSelfTest
java -ea -cp 'kaishek-syntax/target/classes;kaishek-syntax/target/test-classes' com.xenoamess.kaishek.syntax.Phase1SyntaxSelfTest
java -ea -cp 'kaishek-syntax/target/classes;kaishek-syntax/target/test-classes' com.xenoamess.kaishek.syntax.ParserCorpusRoundTripSelfTest --root "$corpus" --require-corpus
java -ea -cp 'kaishek-cli/target/classes;kaishek-cli/target/test-classes;kaishek-validator/target/classes;kaishek-ck3-11906-profile/target/classes;kaishek-profile-api/target/classes;kaishek-syntax/target/classes' com.xenoamess.kaishek.cli.KaishekCliSmokeTest
mvn -o "-Dmaven.repo.local=$m2" -DskipTests package
java -jar kaishek-cli/target/kaishek-cli-0.1.0-SNAPSHOT.jar corpus "$corpus"
py -3 -m unittest discover -s kaishek-zg361-profile/tests -v
py -3 kaishek-zg361-profile/tools/validate_domains.py
```

`mod_zhongguo_style` 不在独立仓库内；它是 CK3 资料目录的外部输入。若该目录不可用，
去掉 `--require-corpus` 可运行 parser smoke，但结果只能记为 `SKIP`，不能把缺失 corpus
算作 27 文件通过。

当前观测：

- Maven reactor：`BUILD SUCCESS`；profile-api 4、CK3 profile 5、validator 11、IR 7、
  runtime 19、diff 2、zg361 synthetic 4 个 JUnit 测试全部通过（合计 52）。Syntax/CLI 的无框架
  smoke 主类也由上面的显式命令运行（CLI smoke 无输出即表示通过），Maven 报告其
  JUnit 测试数为 0，这是有意的 dependency-free 设计。
- Parser corpus：27 个 `.txt/.gui` 文件、2,677,440 bytes、0 个错误诊断、字节级
  round-trip 一致。
- Parser property/fuzz smoke：固定 seed `0x4b41495348454b31` 的 760 个生成 case
  全部通过；该结果可重复，但不等于无界或穷尽式 fuzz 证明。
- CLI corpus：27/27 parsed、0 errors；确定性 corpus SHA-256 为
  `30d63aad6adbe60a5df610dca4dcb2592f4e8d6dc5edb9a1d411744571cd687c`。
- Manifest：[`corpus-manifest.json`](corpus-manifest.json) 由生成器重建后无 diff；
  manifest 以显式 LF 写出，SHA-256 为
  `b219a2ee0ef3c9b77fbc0d28a7c99714e6e8b000b84bf4403d352999e3896ac3`。
- 361 schema：Python structural validator 与 8 个单元测试通过，覆盖 38 个 domain
  和机制 ID 1–361；Java validator 仅提供同一结构合同的无框架投影。
- Synthetic 014：BOM `.txt` 经 Parser → Validator → Strict IR → RuntimeKernel/IrExecutor
  走通 `delivered → appeal_open → closed`；未知 opcode、解析错误和 CK3 未认证语义均
  fail-closed。该结果属于离线 synthetic fixture，不是 CK3 live 或差分认证。
- Parser 对非法 UTF-8 现在发出 `INVALID_BYTE` 并保持原始字节；CLI `validate` 在语法错误
  时返回 `INVALID`/exit 1，只有语义层确实不可用且输入无错误时才返回 `UNSUPPORTED`/exit 4。
- 对显式登记参数集合的 opcode，profile/validator 同时校验参数数量和参数名；未知参数以
  `INVALID_PARAMETERS` fail-closed。空集合表示该 opcode 的参数形状仍是多态/未查明，
  此时只做已知的数量与结构检查，不把未认证字段误报成已支持语义。CST/validator 层保留
  参数块的有序重复字段，不按普通映射重复键报错；嵌套 executable sequence 同样保留重复
  opcode，只有文件根声明层采用重复定义诊断。Phase 0 StrictIrCompiler 的命名参数 IR
  仍是 `Map<String, IrValue>`，因此遇到重复参数会产生 `DUPLICATE_PARAMETER`/`INVALID_INPUT`
  并停止发射指令；这是显式 fail-closed 的 IR 边界，不是静默覆盖，待 ordered named-argument
  IR 合同冻结后再开放。
- CK3 1.19.0.6 profile 已按目标 corpus 校正 `change_variable { name, add }`、
  `trigger_event { id, days }` 和资源表达式 block；更广的 vanilla 多态签名仍待
  exact-build schema 证据，不能据此宣称完整 CK3 覆盖。

## Readiness 与未完成项

| 能力 | 当前状态 | 证据边界 |
|---|---|---|
| M0 工程/合同基线 | `static-ready`（正式记录已形成） | 构建、GPL-3.0-only 许可证、manifest 和合同已落盘；签核边界见 [M0 正式 readiness 记录](decisions/0003-m0-formal-readiness.md) |
| M1 lossless parser | `static-ready` | corpus round-trip、Phase1SyntaxSelfTest、malformed/GUI smoke 与固定 seed 的 760-case property/fuzz；更广 mutation/fuzz 矩阵仍待扩展 |
| M2 静态 validator | `static-ready` | 小型 profile/schema fixture；尚未覆盖全部历史故障 |
| M3 Runtime | `static-ready`（原语 + synthetic 014 fixture） | finite draw tape、stale/receipt/queue 合同与一个有限状态切片；不代表 CK3 语义 |
| M4 361 离线闭环 | `static-ready`（仅 synthetic 014 子集） | 已有一条生成 `.txt` → parser → validator → IR → VM 证据；完整 B2/Workforce exact-build 范围尚未开始 |
| M5 CK3 MCP 差分 | `not-available` | 没有 paused pre/post artifact，认证集合为空 |
| Quarkus 服务 | `scaffold-only` | 只保留外壳，按路线图延后依赖与适配器 |

M0 的静态评审已按上述记录收口；下一步是扩展 B2/Workforce exact-build 范围的 361
纵向切片。完整 property/fuzz、历史 mutation、MCP 差分和 CK3 paused artifact 仍是后续
工作。在真实 paused artifact 出现前，不得把任何 ACK、fixture 或单元测试升级为
`fixture-live`、`differential-certified` 或 `product-live`。
