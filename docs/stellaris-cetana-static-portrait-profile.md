# Stellaris 4.4.6 合成女王静态肖像 profile 切片

## 目标与证据

目标 Mod `synthetic_queen_beautification` 依据本机 Stellaris Pegasus v4.4.6 (fdde) 的 `gfx/portraits/portraits/21_portraits_cybernetics_synthqueen.txt` 改用静态 `texturefile` 肖像。该 Mod 的灰风先例 `xenoamess_gray_wind_portraits.txt` 已使用 `portraits = { key = { texturefile = "...dds" } }` 结构。本切片只验证该文件的 10 个原版合成女王肖像键及静态图片路径/问候声音字段的有限形状，不宣告游戏运行时和所有肖像语法已被认证。

## 范围与设计

- 仅 Stellaris profile 把精确文件 `gfx/portraits/portraits/21_portraits_cybernetics_synthqueen.txt` 分类为 `PORTRAITS`；其他肖像文件和其他游戏 profile 不因本切片获得新语义。
- 只允许 `synth_queen`、`cetana_mammalian`、`cetana_reptilian`、`cetana_aquatic`、`cetana_lithoid`、`cetana_plantoid`、`cetana_molluscoid`、`cetana_avian`、`cetana_empty`、`cetana_robot` 十个声明名。
- `portraits = { ... }` 是唯一 root，十个键都必须存在且不可重复。每个声明须是 block，且本切片包含 `texturefile` 和 `greeting_sound` 两个字段；多余字段、拼错的声明名、拼错的字段、重复或缺少字段都 fail-closed。`texturefile` 必须是 `gfx/models/portraits/` 下的带引号 `.dds` 路径，`greeting_sound` 为带引号标识符。目标 DDS 存在性、图片头格式的交叉检查由调用仓库负责。
- 该切片仅是 static/schema 验证，不解释贴图实际加载顺序、人物绘制、动画或存档语义。Stellaris 简体中文实机仍是最终权威。

## 验收标准

1. 用本切片对目标 Mod 的生产肖像文件运行 `validate --profile stellaris-4.4.6 --file ...`，语法和语义 diagnostic 均为 0，状态 `VALIDATED`。
2. 正向单测覆盖精确路径分类和静态肖像形状；反向单测覆盖错误目录、拼错声明、拼错字段、缺失字段、重复字段及错误 DDS 路径。肖像词汇在其他目录仍返回 `WRONG_DOMAIN`。
3. 工具全仓库静态验收通过；原有 profile 对其他目录的 fail-closed 结果不退化。

## 本轮结果（2026-09-25）

- `mvn -o -ntp -pl kaishek-stellaris-446-profile -am test` 通过，Stellaris profile 12 项测试 0 失败。
- `py tools/run_static_acceptance.py` 全仓库离线静态验收通过，含 Maven/JUnit、domain、parser 和 CLI smoke。
- 以重建后的 CLI 对合成女王 Mod 生产肖像文件运行 `validate --profile stellaris-4.4.6`，返回 `VALIDATED`，`syntaxDiagnostics=0`、`semanticDiagnostics=0`。
