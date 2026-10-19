# Stellaris 武器 CSV 包级静态合同

## 目标与范围（实施前）

调用方吞噬之心为 `EEP_PLANET_KILLER_DEVOURER` 新增独立武器统计 CSV，
原有整包检查只处理 P 脚本、DDS 和本地化，不能证明该文件有效。先补充工具能力，
完成工具回归、提交和推送，再恢复调用方 Mod 验收。

检查范围只包含 Mod `common/component_templates` 下的 `.csv`，作为本版本原生武器
统计表合同；其它目录的 CSV 不在范围内。没有这些 CSV 的历史 Mod 明确报告不适用，
不新增必须拥有 CSV 的要求。本合同只验证静态格式和键关联，不执行武器、伤害、
充能、开火或游戏资源加载语义。

## 原版证据

本机 `C:\SteamLibrary\steamapps\common\Stellaris`，Cygnus 4.5.2（9776）：EXE SHA-256
`400df27c82ddc845aa9dce79bd468d81f18f060d299cd263e3afa93aef4f7a83`。
只读核对两个原生 `common/component_templates` 文件：

| 文件 | SHA-256 | 数据行 |
| --- | --- | ---: |
| `weapon_components.csv` | `a0d4b199ea51e59a600beab99046fad3c8a11a49ae39c8ace0aed73ebf6ff330` | 344 |
| `mutation_weapon_components.csv` | `759fe5e7d82927b91c75838b37726bd1fd79d63f0be24bba428403734b032b72` | 247 |

两者都是 UTF-8 无 BOM、分号分隔、25 列；以 `#` 开头的整行是注释，注释既可能
带分号补齐，也可能只有一列。首个非注释、非空行为同一表头：

```text
key;cost;power;min_damage;max_damage;hull_damage;shield_damage;shield_penetration;armor_damage;armor_penetration;min_windup;max_windup;cooldown;range;accuracy;tracking;size_damage_factor;military_power_multiplier;missile_speed;missile_evasion;missile_shield;missile_armor;missile_health;missile_retarget_range;end
```

数据行末列 `end` 为空，即保留尾分号。原版存在负 `power`，mutation 表全部 247 行
`power` 留空，另有导弹数值留空；不把这些已存在的格式当错误，不推断空值默认值。
`PLANET_KILLER_*` 使用 `min_windup=90`、`max_windup=0`、`cooldown=30`，
`PLANET_KILLER_MAW` 的 `power=0`。这些数值是本次格式调查证据，不是通用数值约束。
`00_weapons_planet_killer.txt` 和 `00_biogenesis_crisis_components.txt` 中的
根级 `weapon_component_template` 通过直接 `key` 字段关联表中键。

## 设计

- 拒绝 Mod CSV BOM、非法 UTF-8、错误表头、非 25 列行、空 key、非空 `end`、
  Mod 各文件之间及单文件内重复 key。非空数值必须是有限十进制数；空数值沿用
  原版格式，负值不额外设业务限制。当前受支持合同使用普通未加引号的分号单元格，
  不把其它 CSV 方言当作已确认格式。
- 对组件脚本做仅用于键关联的词法投影：跳过注释、识别字符串与花括号，提取根级
  `weapon_component_template` 内直接 `key` 字面量。仍由已有 Kaishek CLI 负责
  P 语法／往返检查；投影不能把注释、嵌套 `key` 或字符串伪装成组件声明。
- Mod CSV 的每个键须有 Mod 或未被同路径文件遮盖的原版武器定义；有 CSV 时，
  Mod 的每个武器定义须能在 Mod 或未被同路径文件遮盖的原版 CSV 中找到统计。
  Mod 覆盖原生键本身合法，不将 Mod 和原版的同键视作 Mod 内重复。
- 原版有 36 个通过 `inline_script` 生成 key 的武器块。本合同不展开这些块，
  报告其数量，不因无关原生生成模板直接失败；如果实际需要的 CSV key 不能通过
  直接声明匹配，仍报告缺定义，不能借此推测生成结果。Mod 的非字面量声明明确失败。
- 报告 CSV 文件数、统计键数、适用性和明确的静态能力边界。不验证组件潜在条件、
  图形、武器类型语义、CSV 数字在引擎中的单位或脚本变量求值；非字面量 key 必须
  明确诊断为当前关联合同不支持。

## 验收标准

有效独立单行 CSV（包含注释、负功耗和原版允许的空数字）通过；单文件／跨文件
重复、错误表头／列数、NaN／Infinity／非数字、空 key／非空 end、UTF-8／BOM
故障均精确拒绝。覆盖缺定义、缺统计、原生定义／统计回退及同路径覆盖隐藏原生
来源的负例；注释和嵌套 key 不得产生假引用；无 CSV 历史包保持通过并报告不适用。

运行 `py -m unittest tools.test_accept_stellaris_mod -v` 和 README 要求的
`py tools/run_static_acceptance.py`。在工具库提交推送后，用调用方当前 Mod 做一次
只读静态检查；调用方最终包仍须自行重跑及使用简体中文完成实机验收。

## 实施与结果

初始工作树干净。开始工作后 fetch 发现远端包含两条 scope guard 修复，已快进至
`2b55996` 再整合本次补丁，保留有限人口作用域和脚本值合同。首轮 33 项包回归中的
原生 inline 计数用例因夹具与 Mod 同路径而被正确遮盖；将该原生夹具改为独立文件名，
继续验证真实未遮盖的 inline 边界。原生文件同路径遮盖的独立负例保持。

修正夹具后，12 项独立 CSV 回归全部通过。完整
`py tools/run_static_acceptance.py` 返回 `exit_code=0` 和 `static acceptance: PASS`：
metadata、Maven clean/package、domain、parser/fuzz、packaged CLI smoke、33 项整包
回归（包含新增 12 项 CSV 专项及 1 项包入口集成）与 33 项 scope guard 回归通过。
没有启动 CK3／Stellaris，也未扫描外部 Mod corpus。命令结果摘要见
[离线验收证据](evidence/stellaris-weapon-csv-2026-10-19/static-acceptance-summary.txt)。

报告字段为 `weapon_csv_files`、`weapon_csv_keys`、`weapon_csv_applicability`；有 CSV 时
还报告 `weapon_csv_native_unexpanded_templates`。调用方必须识别这些字段，修复前的
旧 PASS 不能作为武器 CSV 验收证据。当前 Mod 的独立表只读专项检查为 1 文件、1 键、
`checked` 且零错误。

工具修复提交 `d5e61a9` 已推送 `origin/main` 后，用当前调用方
`eat_everything_origin` 只读重跑整包入口：`PASS`、退出码 0、34/34 P 文件、12 DDS、
114 个本地化键；`weapon_csv_files=1`、`weapon_csv_keys=1`、
`weapon_csv_applicability=checked`、未展开原生模板 36、`errors=[]`。记录见
[整包静态报告](evidence/stellaris-weapon-csv-2026-10-19/devourer-package-report.json)及
[关键输入指纹](evidence/stellaris-weapon-csv-2026-10-19/devourer-input-manifest.json)。
独立表 SHA-256 为 `f00d024a4714fd283629a94a401a3956e5317aa7e0693f03aacd7ed902140428`。
这是工作中快照的静态校验通过，非中文运行时不在范围内；未启动任何游戏，调用方
仍须对最终包重跑工具，并另行完成简体中文实机验收。
