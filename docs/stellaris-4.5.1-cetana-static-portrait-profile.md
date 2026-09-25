# Stellaris 4.5.1 合成女王静态肖像验收切片

## 目标与事实

日期：2026-09-25。`xenoamess_stellaries_dev/synthetic_queen_laoda_replacement` 需要在已更新的本机 Stellaris `Cygnus v4.5.1 (358e)` 验收。EXE SHA-256 为 `6fe06709f265e726722dc23f617c5fc4e5557629e2d43fe312016aba547c83e4`。该版本原版 `gfx/portraits/portraits/21_portraits_cybernetics_synthqueen.txt` 的 SHA-256 为 `9a2ccd43d297112176092e62a9e488b63a8ca271915d028b84e14491724fee18`，与已核实的 4.4.6 文件逐字节相同；十个声明键、`greeting_sound` 和动态材质路径仍在同一位置。

现有 `stellaris-4.4.6` profile 正确拒绝冒充 4.5.1 的验收身份；本切片新增独立 `stellaris-4.5.1` profile，仅复用前述**字节相同的合成女王肖像文件**已验证的静态结构。该版本其它 Stellaris P 语言目录或 opcode 没有经此调研确认，不得继承 4.4.6 的广泛静态语义。

## 设计与边界

- 新 profile 锁定 4.5.1 EXE 指纹，只把精确相对路径 `gfx/portraits/portraits/21_portraits_cybernetics_synthqueen.txt` 分类为 `PORTRAITS`；其它路径一律 `UNKNOWN`。
- 只注册旧 profile 的 `portraits`、十个原版合成女王肖像键、`texturefile`、`greeting_sound` 的原有 schema。新 profile 不注册事件、决议或其他 opcode。
- 验证器的合成女王必需键、字段、重复键、DDS 路径规则明确适用于两个精确 profile ID。其余领域诊断逻辑不扩展到 4.5.1。
- CLI 的 `validate` 与 `profile --id` 对该 profile 提供明确身份；报告 `runtime=UNSUPPORTED`，游戏运行时仍由 Stellaris 实机检查。

## 验收标准

1. `stellaris-4.5.1` 对目标 Mod 生产肖像文件返回 `VALIDATED`，语法及语义诊断均为 0；错误键、错误字段、缺字段及错误路径被拒绝。
2. 对其他目录和 4.4.6 其它 opcode 维持 fail-closed；4.4.6 原 profile 不退化。
3. 相关 Maven 测试、CLI 冒烟测试及全仓库静态验收通过；工具更改先提交推送，才继续 Mod 实机验收。

## 检查结果

- `mvn -o -ntp -pl kaishek-cli -am package` 成功；新增 `StellarisProfile451Test` 两项及既有 `StellarisProfile446Test` 十二项均通过。
- `python tools/run_static_acceptance.py` 全仓库离线静态验收 `PASS`，包括 clean package、CLI smoke、合成夹具与元数据检查。
- 重建 CLI 的 `profile --id stellaris-4.5.1` 回报正确 EXE 指纹和 `runtime=UNSUPPORTED`；目标 Mod 生产肖像文件用 `validate --profile stellaris-4.5.1` 返回 `VALIDATED`，`syntaxDiagnostics=0`、`semanticDiagnostics=0`。

## 实机发现的 BOM 漏检及修复目标

第一次 4.5.1 隔离实机检查发现目标 Mod 的生产 `21_portraits_cybernetics_synthqueen.txt` 以字节 `EF BB BF` 开头：PowerShell 旧版 `Set-Content -Encoding utf8` 写入了 BOM。CLI 曾将该文件判为 `VALIDATED`，但 Stellaris `portraits.cpp:963` 报 `Unexpected token: ﻿`，并导致 `cetana_*` 肖像键无法找到。这是该静态切片的实际漏检，必须先修工具再继续 Mod 验收。

修复范围仅限 `stellaris-4.4.6`、`stellaris-4.5.1` 的精确合成女王肖像路径。CLI `validate` 在解析前检查原始字节开头的 UTF-8 BOM，返回 `INVALID`，携带独立诊断码 `STELLARIS_PORTRAIT_UTF8_BOM` 与非零语法诊断数；对无 BOM 同内容保持通过。其他 profile/目录的编码规则未在此证据中建立，不扩展判断。增加有/无 BOM 的 CLI 冒烟测试；全仓库静态验收通过后提交推送，再修 Mod 文件。

修复完成：`python tools/run_static_acceptance.py` 全仓库离线静态验收再次 `PASS`，含新增的无 BOM 通过、有 BOM 对 4.4.6 和 4.5.1 均拒绝的 CLI 冒烟检查。重建 CLI 直接检查实际带 BOM 的目标 Mod 文件，返回 `INVALID`、`syntaxDiagnostics=1`、`syntaxCodes=["STELLARIS_PORTRAIT_UTF8_BOM"]`、退出码 1，准确复现了实机加载阻断。工具库变更提交并推送后再继续修 Mod。
