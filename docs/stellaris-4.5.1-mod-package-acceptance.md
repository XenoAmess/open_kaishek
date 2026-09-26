# Stellaris 4.5.1 Mod 包级静态验收

## 目标与边界

`xenoamess_stellaries_dev/shishan_code_origin` 使用起源、局势、特殊项目、事件、脚本值、物种特质、建筑与本地化。现有 `stellaris-4.5.1` profile 只验证合成女王静态肖像，无法覆盖此 Mod。增加可复用的包级检查入口，使用本仓库 CLI 的 P 语言解析器检查 Mod 中全部脚本，叠加文件、版本、资源路径和十语言本地化的结构合同。它不给未注册 opcode 冒充语义认证；游戏行为仍须在 Stellaris 实机验证。

## 设计

- `tools/accept_stellaris_mod.py --mod <path> --game <path> --report <path>`：明确传入 Mod 根和游戏安装路径；只读取两者，报告另存调用者指定位置。检查精确游戏 EXE SHA-256、`VERSION` 与描述符、脚本 UTF-8 无 BOM、每个 `.txt` 由 `kaishek-cli parse` 返回 `PARSED` 且零诊断和可往返、DDS 头、引用的本地资源存在。
- 本地化：从游戏 `localisation` 目录推导官方语言目录，要求 Mod 每种语言的 `l_<language>` 文件带 UTF-8 BOM、键集合相同且无重复/空值；保护 `$...$`、`§.`、转义序列；非中文不允许含中文占位符。生成静态报告，不声称运行时通过。
- 未识别的资源引用只对本 Mod 显式路径检查；原版 `GFX_*` 等引用不被当成本地文件。对缺路径、解析失败、键缺失、BOM 错误提供明确诊断码。
- 添加工具自身的正反测试：合格最小夹具通过；脚本 BOM、P 语法损坏、描述符版本偏离、缺译文键、破损资源路径均失败。运行工具仓库现有全量静态验收。

## 验收标准

1. 工具用本机已验证的 Stellaris Cygnus 4.5.1 EXE 指纹执行，输出包级 `PASS`；Mod 全部 P 文件都由 Kaishek CLI 解析，并记录解析文件数与失败数。
2. 负例各自失败且报告对应路径与诊断；不得将单纯解析通过描述成 opcode 或运行时语义通过。
3. 测试、全仓库静态验收通过，工具改动先在本仓库提交推送，然后原 Mod 仓库才继续最终验收。

## 调查与结果

- 2026-09-26：既有 `stellaris-4.5.1` profile 的覆盖限于 `gfx/portraits/portraits/21_portraits_cybernetics_synthqueen.txt`，对本次 Mod 的其它脚本路径返回 `UNKNOWN_DIRECTORY`。包级独立入口是实现广目录语法与资产合同的受限方案；完整 opcode 合同仍由实机错误日志和专项测试承担。
- 工具实现：新增 `tools/accept_stellaris_mod.py`，逐文件调用 Kaishek CLI `parse`，检查 4.5.1 EXE 身份、SemVer/描述符、脚本 BOM、DDS 及引用、游戏列出的全部本地化语言与键/格式 token。`semantic_scope` 字段明确声明此处只覆盖语法和包结构。
- 工具回归：`py -m unittest tools/test_accept_stellaris_mod.py -v` 3 项通过，包含合格包、版本/资源故障、翻译键/脚本 BOM 故障；已接入 `tools/run_static_acceptance.py`。全仓库 `py tools/run_static_acceptance.py` 返回 `static acceptance: PASS`，新测试亦在该流程中通过。
