# Stellaris 巨像摧毁通知的动态本地化键

目标：阻止实际UI中已确认的原始键泄漏。Stellaris 4.5.2 / EXE
400df27c82ddc845aa9dce79bd468d81f18f060d299cd263e3afa93aef4f7a83
对原生planet_killer组件EEP_PLANET_KILLER_DEVOURER生成摧毁通知时，读取
MESSAGE_DESC_FOR_EEP_PLANET_KILLER_DEVOURER。Mod缺该键仍通过既有包检查，
但简中实机tooltip显示原始键。原件位于Stellaris项目run20261019T204055Z的
r9li-native-reward-notification-tooltip.jpg/.ocr.json，原失败不能删除。

范围：只扩展tools/accept_stellaris_mod.py的静态合同及对应Python测试。
沿用现有literal planet_killer声明投影，不实现新P解析器或消息执行器。
每个实际游戏语言必须在Mod或同语言原版文件中解析到非空描述键；允许原版
回退，拒绝缺键、空值和把原始键直接当正文。无planet_killer组件明确不适用。
保持现有key-set/BOM/encoding/token/非中文占位检查，报告新增required/resolved
计数及有限合同名称，不将静态通过说成原生UI测试。

验收：新增测试覆盖单语言漏键、多语言完整、原版回退、空/原始键正文及无组件。
原工具完整Python测试通过；针对真实RC7包产生新增缺键FAIL，再由项目补齐
十语言后重新产生PASS。修复先在本工具仓库提交推送，随后才能恢复Mod验收。
这是Stellaris特有的组件消息键约定，不推广到CK3或通用P语言。

## 修复验证

最终源码执行`python -B -m unittest tools.test_accept_stellaris_mod -q`：59项通过，
19.371秒。真实RC7包36/36 P解析、12 DDS、131键保持，但新通知合同10语言
required10/resolved0，恰好十条PLANET_KILLER_MESSAGE_LOCALIZATION_MISSING，
顶层FAIL，工具退出1。原回归报告原字节归档在
`evidence/2026-10-20-stellaris-pk-message-localization/`，SHA
abf763205c42b9d3894b1bbc4291e02039ea63855addff906311a633ddba25d3。
尚未把Mod修正或UI复测当作已通过；后续新包报告另存不覆盖本失败。
