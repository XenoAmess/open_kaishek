# Stellaris 4.4.6 灰风美化静态 profile 切片

## 目标

为外部 `[XenoAmess的灰风美化]` Mod 增加一个严格、受限的静态验证切片，使其自有的兼容修复事件和 monthly on_action 能由 `stellaris-4.4.6` profile fail-closed 验证。

本切片不尝试把 Stellaris 全量事件语言注册为“已支持”。完整的 4.4.6 原版覆盖文件仍由 lossless corpus parser、调用方的原版基线差异合同和游戏实机共同验收。

## 范围

新增且只新增已在 Stellaris 4.4.6 原版或目标 Mod 中观察到的形状：

- 事件结构字段：`hide_window`、`is_triggered_only`、`immediate`；
- on_action 事件列表容器：`events`；
- trigger：`is_ai`、`has_country_flag`、`has_leader_flag`；
- 迭代/作用域结构：`any_owned_leader`、`every_owned_leader`、`event_target:gray_official`、`event_target:gray_country`、`ruler`；
- effect：`change_leader_portrait`。

已有 `OR`、`if`、`limit`、`trigger`、`exists` 等通用结构继续复用当前 profile/API。

## 非目标

- 不宣告事件调度、global event target、领袖肖像变更或存档迁移的 runtime 语义已经认证；这些结论必须来自 Stellaris 实机。
- 不为任意 `event_target:*` 建立通配放行；仅接受本目标中显式登记的两个目标名。
- 不放宽未知 opcode 的 fail-closed 行为。
- 不把 `events/gray_goo_events.txt`、`common/anomalies` 或 `interface` 的完整原版词汇整体加入 profile。

## 验收

1. 目标兼容事件和 on_action fixture 均为 0 个 ERROR diagnostic。
2. 拼错的领袖 flag、肖像 effect、event target 仍返回 `UNKNOWN_OPCODE`。
3. 已有 Stellaris profile 测试及全仓库静态验收全部通过。
4. `profile --id stellaris-4.4.6` 继续明确 runtime 为 `UNSUPPORTED`。

