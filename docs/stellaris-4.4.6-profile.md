# Stellaris 4.4.6 静态 profile 边界

## 目的

`stellaris-4.4.6` 是为首个外部 Stellaris mod 验收增加的受限静态 profile。它绑定本机
Pegasus 4.4.6 可执行文件 SHA-256
`BC451C72D9654C8901F1BB0BEE1DD78D76F415465C2FBF746E9F98ADE333173A`，并让 CLI 能对
`common/decisions` 和 `common/deposits` 中已经观察到的脚本形状执行 fail-closed 验证。

## 当前覆盖

- 目录：`common/decisions`、`common/deposits`，以及 I1-001 使用的受限
  `common/scripted_triggers` 形状。
- 决议结构：`owned_planets_only`、`enactment_time`、`resources/category/cost/minerals`、
  `ai_weight/weight`、`effect/add_deposit`。
- 地块结构：`is_for_colonizable`、`planet_modifier`、`triggered_planet_modifier`、
  `potential`、`exists`、`owner/is_gestalt`、`country_modifier`、`modifier`、`always`、`drop_weight`、
  `should_swap_deposit_on_terraforming`。
- `planet_modifier`、`modifier` 和 `drop_weight` 的子项是动态 modifier/value 键映射，
  profile 把这些参数块视为不透明容器；它不据此声明每个岗位 modifier 的引擎语义。
- 方舟兼容切片：决议 `potential` 中的 `vivhite_workplace_supported_colony` 调用，及其
  scripted trigger 声明所需的 `OR`、planet-scope `is_planet_class`、country-scope
  `is_nomadic`。该切片只接受已登记的目标 Mod trigger 名，不把任意未知自定义调用放行。
- I1-002 菜单状态切片：planet-scope `has_planet_flag` trigger，以及 planet-scope
  `set_planet_flag`、`remove_planet_flag` effect。它们只提供 4.4.6 静态形状检查；flag
  是否随存档持久化及两个零工期决议是否即时互斥，仍由 Stellaris 实机夹具验证。

未登记的可执行键仍报告 `UNKNOWN_OPCODE`，未识别目录仍报告 `UNKNOWN_DIRECTORY`。

## 证据等级

该 profile 只提供 `static` 级解析和 schema 验证。所有条目都没有 runtime handler、
exact-build differential artifact 或认证语义；`profile --id stellaris-4.4.6` 因此明确返回
`"runtime":"UNSUPPORTED"`。Stellaris 实机加载、决议可见性、执行效果、存档持久性和
特殊政体行为必须由外部隔离实机夹具另行验证。

当前 validator 不沿 `owner` 等 scope link 做完整静态 scope 推导；这里登记的 PLANET/COUNTRY
只固定 opcode 合同，不能替代方舟实机验证。未知 opcode 仍须 fail-closed，以防拼写错误被
当成合法自定义 trigger。

I1-002 对 flag 名采用标量值形状。当前通用 validator 能区分 trigger/effect kind、目录与
显式 block 参数，但尚未对每个 opcode 的标量 token 类型建立独立模型；因此本切片用正向
profile 测试固定三种已观察操作，并用拼错的 flag opcode 负例固定 fail-closed 边界，不把
“任意标量值均已得到语义认证”写入能力声明。

## 使用

```powershell
java -jar kaishek-cli/target/kaishek-cli-0.1.0-SNAPSHOT.jar validate `
  --profile stellaris-4.4.6 --file <script.txt>
```
