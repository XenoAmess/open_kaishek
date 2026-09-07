# CLI smoke classpath 回归与修复方案

## 事故

`core-ci` 从提交 `2e17cd8`（新增 Stellaris 4.4.6 profile）开始失败。Maven 单元测试与模块构建成功，随后 `Run dependency-free parser and CLI smoke` 步骤抛出：

```text
java.lang.NoClassDefFoundError: com/xenoamess/kaishek/profile/StellarisProfile446
```

`kaishek-cli/pom.xml` 已声明 `kaishek-stellaris-446-profile` 依赖，CLI 也会实例化该 profile；但 workflow 为裸 `java -cp` 另行维护了一份模块目录列表，该列表没有加入新模块。后续提交没有改变这个 CI 入口，因此相同故障持续存在。

## 原设计目的与缺陷

显式启动 main class 的 smoke 用于证明 parser 和 CLI 可以脱离 Surefire、IDE 及测试框架运行。裸 JVM 不读取 Maven `pom.xml`，所以旧 workflow 手工列出各模块的 `target/classes`。

这一做法把 CLI 依赖图复制到了 CI：Maven 依赖声明与 workflow classpath 必须人工同步。两者一旦漂移，`mvn test` 可以通过，裸 CLI smoke 却会在运行时缺类。给旧字符串追加 Stellaris 目录只能修复当前实例，不能消除重复真源。

## 修复设计

1. Maven 的首次完整检查从 `test` 提升为 `package`，在运行全部测试的同时生成由 `maven-shade-plugin` 装配的自包含 CLI JAR。
2. parser 的四个无依赖 main 继续直接使用 syntax 模块自身的 classes/test-classes；它们没有跨模块依赖图。
3. CLI smoke 使用 `kaishek-cli/target/test-classes` 加自包含 CLI JAR；正式 CLI preflight 直接使用 `java -jar`。
4. `tools/run_cli_smoke.py` 必须检查恰好找到一个 CLI JAR，避免版本变化时维护硬编码文件名，也避免静默选择错误产物；workflow 与 README 共用该入口。
5. README 的手工 smoke 命令同步改为调用共享入口，删除第二份模块依赖列表。

CLI 依赖的唯一真源因此恢复为 `kaishek-cli/pom.xml`；以后新增 profile 或其他模块依赖时，Shade 产物与 smoke 会自动随 Maven 依赖图更新。

## 验收标准

- `mvn -B -ntp package` 成功并运行全部 Maven 测试。
- Python schema tests 与 domain validator 成功。
- 四个 parser main 成功。
- `KaishekCliSmokeTest` 通过 `target/test-classes + shaded JAR` 成功。
- `KaishekCli preflight --fixture synthetic-361-014` 通过 `java -jar` 成功。
- 两次 clean package 的 JAR SHA-256 清单完全一致。
- `git diff --check` 成功。
- 推送后 GitHub Actions `core-ci` 成功；在远端状态变绿前不得宣称修复完成。
