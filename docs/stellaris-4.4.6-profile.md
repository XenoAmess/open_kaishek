# Stellaris 4.4.6 静态 profile 边界

## 目的

`stellaris-4.4.6` 是为首个外部 Stellaris mod 验收增加的受限静态 profile。它绑定本机
Pegasus 4.4.6 可执行文件 SHA-256
`BC451C72D9654C8901F1BB0BEE1DD78D76F415465C2FBF746E9F98ADE333173A`，并让 CLI 能对
`common/decisions` 和 `common/deposits` 中已经观察到的脚本形状执行 fail-closed 验证。

## 当前覆盖

- 目录：`common/decisions`、`common/deposits`。
- 决议结构：`owned_planets_only`、`enactment_time`、`resources/category/cost/minerals`、
  `ai_weight/weight`、`effect/add_deposit`。
- 地块结构：`is_for_colonizable`、`planet_modifier`、`triggered_planet_modifier`、
  `potential`、`exists`、`owner/is_gestalt`、`country_modifier`、`modifier`、`always`、`drop_weight`、
  `should_swap_deposit_on_terraforming`。
- `planet_modifier`、`modifier` 和 `drop_weight` 的子项是动态 modifier/value 键映射，
  profile 把这些参数块视为不透明容器；它不据此声明每个岗位 modifier 的引擎语义。

未登记的可执行键仍报告 `UNKNOWN_OPCODE`，未识别目录仍报告 `UNKNOWN_DIRECTORY`。

## 证据等级

该 profile 只提供 `static` 级解析和 schema 验证。所有条目都没有 runtime handler、
exact-build differential artifact 或认证语义；`profile --id stellaris-4.4.6` 因此明确返回
`"runtime":"UNSUPPORTED"`。Stellaris 实机加载、决议可见性、执行效果、存档持久性和
特殊政体行为必须由外部隔离实机夹具另行验证。

## 使用

```powershell
java -jar kaishek-cli/target/kaishek-cli-0.1.0-SNAPSHOT.jar validate `
  --profile stellaris-4.4.6 --file <script.txt>
```
