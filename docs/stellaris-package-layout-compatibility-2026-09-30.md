# Stellaris 整包验收目录兼容修复

## 目标、范围与证据

2026-09-30 对 Stellaris 4.5.1 五个生产 Mod 检查时发现整包入口的三处不足：纯美术包没有新增本地化文件却被要求十语言；无限岗位使用游戏可加载的平铺 `localisation/*_l_<language>.yml`，被子目录限制遗漏；该包的 VERSION 在综合仓库根目录，需要显式指定来源。灰风和合成女王包全部脚本解析通过，仅因不存在文本而失败；无限岗位已有十语言文件却被报缺失。

## 设计

- 本地化只在包含 `.yml` 时执行十语言完整性检查；递归按语言文件后缀寻找文件，兼容平铺、语言子目录与 replace。没有本地化时明确报告不适用，不能把有文件但缺语言当成无文本包。
- 新增可选 `--version-file` 参数，调用者显式提供 VERSION。默认行为保持原合同，禁止向祖先目录任意搜索或忽略缺失来源。
- 已有无限岗位本地化使用 `key: "value"`（无修订数字），且历史 Stellaris 实机报告证实可加载；语法合同同时接受该形式与 `key:0 "value"`，继续拒绝缺冒号或缺引号。
- 以已锁定游戏 `launcher-settings.json.modsCompatibilityVersion` 检查 supported_version 是否覆盖实际游戏；拒绝旧 `4.4.*` 对当前 4.5 的声明。无 launcher-settings 的 synthetic 测试仍以指定 EXE 身份覆盖原合同。
- 工具仅检查语法与包结构，不扩展 opcode/runtime 语义认证。

## 验收标准

真实无文本包通过本地化适用性检查；平铺十语言包通过；有文本缺语言仍失败；外置 VERSION 能严格匹配；过时兼容声明仍拒绝。新增正反回归和全仓库 `run_static_acceptance.py` 通过，提交推送工具库后再继续 Mod 验收。

## 结果

最终 `py tools/run_static_acceptance.py` 为 PASS：Maven clean package、domain、parser/fuzz、CLI smoke 全部通过；包级 Python 回归共 9 项，覆盖目录、无文本、外置版本、过时兼容声明、无修订数字及其负例。工具仍只提供语法与包结构结果。
