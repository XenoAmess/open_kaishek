# Stellaris 巨像数值来源静态合同

## 目标、范围与验收（实施前）

调用方 RC3 原生巨像指令只经过数日便完成，独立 `eep_weapon_components.csv` 中的
90／0／30 没有形成预期时长。此前工具仅验证 CSV 格式和字面量键关联，不能证明
任意 CSV 文件名被游戏载入；旧 PASS 不得作为数值来源有效性的证据。暂停调用方
Mod 验收，先完成本工具修复、验证、提交推送，再恢复 Mod 验收。

本次只补足 Mod 根级 `weapon_component_template`、直接 `type=planet_killer` 的
四项明确数值来源，以及武器 CSV 的受支持路径。仍不解释武器执行、随机预热、
充能／发射阶段、日期推进、修正、inline_script 或动态变量，不宣称静态通过等于
原生 90／30 天行为通过。无 CSV、无巨像的历史 Mod 继续明确报告不适用。

## 一手证据与推断边界

本机 Stellaris 4.5.2 EXE SHA256
`400df27c82ddc845aa9dce79bd468d81f18f060d299cd263e3afa93aef4f7a83`。
`common/component_templates` 仅有 `weapon_components.csv` 与
`mutation_weapon_components.csv` 两个武器统计表。EXE 中有这两条完整相对路径，
同一区域另有 utility／strike_craft 专名路径；没有反汇编调用链证据，单独这些
字符串不足以证明所有 VFS 载入分支。故工具明确限定已支持路径，未知路径失败，
而不是断言已经执行过加载器。

原版 `common/component_templates/000_documentation.txt:9,138-146` 列出 `power`、
`windup`、`total_fire_time`；`00_weapons_energy.txt:10` 明确说明
weapon_components.csv 的数值覆盖脚本值。原版巨像 CSV 行直接提供
min_windup=90、max_windup=0、cooldown=30，故不能把 90／0 当作普通最小值大于
最大值错误。CSV 的 cooldown 在报告中映射到 total_fire_time 数值槽；来源类型
始终记录，映射不解释具体阶段的运行时计时语义。

成熟 Mod 作者仓库的一手实例进一步确认 TXT 块形状：
[Gigastructures 固定提交 aeternum_components.txt](https://github.com/Pouchkinn-s-Gigastructures/Gigastructures/blob/436812fb3acd9dc7d6cb5e666268a3a1d8135aa7/common/component_templates/aeternum_components.txt#L466)
在 PLANET_KILLER_AETERNUM_SHROUD 的直接字段使用
`windup = { min = 30 max = 30 }`、`total_fire_time = 30`，type／size 均为
planet_killer。原文 SHA256
`d2274c452717e1fb5b089e10480881d36804ebe64f0f26350ac780999b0d9256`，
该仓库 descriptor supported_version 为 v4.5.*。这支持字段语法，不替代本机实际
武器时长测试；不将第三方源码复制到工具测试夹具。

## 设计

- 所有 Mod 组件目录 CSV 继续检查原有编码、表头、列数、有限十进制、重复键及
  关联；只有精确相对路径 `common/component_templates/weapon_components.csv`
  和 `common/component_templates/mutation_weapon_components.csv` 可作为本合同
  数值来源。按现有 Windows 路径归一化大小写。未知文件含子目录同名文件均报
  `WEAPON_CSV_UNSUPPORTED_PATH`，不能把语法合格误当已支持载入。
- 巨像数值优先取上述有效路径中同 key 的 CSV 行；Mod 同路径遮盖 native 文件，
  没有遮盖的 native 表可以回退。多表同 key 来源歧义失败。有效 CSV 同 key
  存在时，不因 TXT 另有完整字段而绕过 CSV 的覆盖优先级；必需单元格空值也不
  猜默认值。未知路径 CSV 永不提供数值。
- 没有上述 CSV 同 key 时，要求当前 Mod 巨像声明直接给出一个 windup 块，块内
  恰有 min、max 两个直接字面量，另有恰一个直接 total_fire_time 和 power。
  注释、字符串、嵌套同名字段不算；重复、变量、引号数字、不支持结构、缺值、
  NaN／Infinity／溢出一律明确失败。只支持普通有限十进制；三个时序值非负，
  power 可负；不限制 min<=max，也不强加 90／30 业务常数。
- 多个 Mod 巨像全部检查，重复 key 失败；不展开生成模板。每个已完整解析组件
  记录来源路径、行号、文件 SHA 与四个数值，不能由一个有效巨像掩盖其它缺项。

固定 CSV 路径得到支持不代表本工具展开整份原版的宏生成组件。完整复制本机
weapon_components.csv 会包含 36 个 inline_script 生成键，现有有限关联投影仍可能
报告其定义未解析；本轮不增加保真行豁免或宏求值。native 直接定义的 key 可作为
统计／定义回退且不要求 Mod 重声明。调用方本轮 RC4 使用 0 CSV 的直接 TXT 路径。

报告合同（调用方须精确识别，缺字段旧报告拒绝）：

| 字段 | 含义 |
| --- | --- |
| weapon_csv_loader_contract | `native_fixed_paths_v1` |
| weapon_csv_unsupported_files | 非受支持路径的 Mod 武器 CSV 文件数；通过必须 0 |
| planet_killer_numeric_contract | `native_csv_or_literal_txt_v1` |
| planet_killer_numeric_components | Mod 直接字面量巨像独立 key 数 N |
| planet_killer_numeric_resolved | 完整解析支持来源的组件数 M；通过必须 M=N |
| planet_killer_numeric_applicability | 有组件为 checked，否则 not_applicable_no_planet_killer_components |
| planet_killer_numeric_sources | 每个 resolved key 一项：key、kind、path、line、sha256、values |

kind 枚举为 `literal_component_txt`、`native_fixed_csv`、`mod_fixed_csv`。
values 字段恰为 `windup_min`、`windup_max`、`total_fire_time`、`power`。
原 `weapon_csv_files`／keys／applicability 与三回调报告继续保留，CSV 数量可以为 0。
调用方不得要求固定一个巨像；应比较 N、M、回调组件数量与其包内实际巨像集合。

## 验收标准

正例覆盖无 CSV 的完整 TXT、90／0 哨兵、固定 CSV 与 native 回退、多巨像、
负 power、无巨像历史包。负例覆盖旧任意命名 CSV 90／0／30、嵌套同名文件、
各缺项、重复字段、变量／字符串／嵌套伪装、非有限数、负时序、同 key 来源歧义、
同路径遮盖、CSV 覆盖 TXT 的不完整或冲突值；保留原 CSV 和回调回归。

运行定向 `py -m unittest tools.test_accept_stellaris_mod -v`，再按 README 执行
`py tools/run_static_acceptance.py`。提交推送后，用调用方旧冻结缺陷包只读取得
FAIL，最终生产修复包只读重跑取得新合同证据；旧报告保持原字节，不冒称实机通过。

## 实施与验证

起点工作树干净，HEAD／origin/main 为 7a9a360，fetch 没有新增远端提交。
已与调用方准备／发布适配代理协调上述精确报告合同。新增 12 项独立数值来源回归
及 1 项整包旧 CSV → TXT 替换回归，连同原包／CSV／回调测试共 **56 项通过**。
首轮旧重复键测试受固定表名排序改变影响，已改为核验两条冲突路径集合；检测本身
仍然拒绝跨文件重复。README 全量 `py tools/run_static_acceptance.py` 返回退出码 0，
metadata、Maven clean/package 与 JUnit、domain、parser/fuzz、packaged CLI smoke、
56 项整包回归和 33 项 scope guard 均通过；未启动游戏或扫描外部 corpus。
完整输出见 [static-acceptance.txt](evidence/stellaris-pk-numeric-source-2026-10-19/static-acceptance.txt)。
提交后实际候选报告待补。
