# Stellaris 包级 GUI 脚本覆盖修复

日期：2026-10-07。触发项目：吞噬之心报告入口拟使用 Stellaris 4.5.2 原生 effectbuttonType。Mod 实机验收已暂停，工具修复验证并提交推送后再继续。

## 目标、范围与设计

当前 tools/accept_stellaris_mod.py 只发现 .txt/.gfx 及 descriptor.mod，遗漏 .gui；有效包会在没有解析界面定义的情况下报告 PASS。将 .gui 纳入相同逐文件 UTF-8 无 BOM、Kaishek CLI parse/零诊断/roundTrip、显式 DDS 路径存在性检查与解析数量统计。扩展名大小写与其它脚本一致。保持报告的 syntax/package only 边界，不对 effectbuttonType 的作用域、布局或点击效果冒称运行时认证。

无需扩展 JVM 解析语法；既有 parser 已支持 P 语言 GUI 文件。新增最小自有 GUI 夹具只包含测试窗口与按钮，不复制游戏界面源码。更新包级说明中的发现范围。

## 验收标准

1. 含有效 .GUI 定义的最小包 PASS，发现／解析数包括 GUI，证明并非静默遗漏。
2. 未闭合 GUI 返回 P_SCRIPT_PARSE，路径指向 GUI。
3. GUI UTF-8 BOM 返回 P_SCRIPT_BOM；明确 DDS 路径缺失返回 ASSET_REFERENCE_MISSING，均包含 GUI 路径。
4. 全部现有包级测试及 tools/run_static_acceptance.py 通过。仅工具任务文件提交并推送本仓库远端后再恢复 Mod 验收。

## 结果

已实施：.gui（含大写 .GUI）进入相同解析／编码／资源路径检查。2026-10-07运行 python -m unittest tools/test_accept_stellaris_mod.py -v，13项通过，包含四项新增正反GUI合同；随后 python tools/run_static_acceptance.py 返回exit0及 static acceptance: PASS，覆盖metadata、完整离线Maven/JUnit构建、Python domain、standalone parser/fuzz、CLI smoke及13项包级测试。没有启动CK3／扫描外部corpus，不对Stellaris GUI运行时语义作认证。
