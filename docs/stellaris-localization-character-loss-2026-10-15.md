# Stellaris 本地化字符丢失检查

## 目标与范围（实施前）

Stellaris 4.5.2 Mod实际生成中文“裁决级母舰”时，Windows终端编码转换把非ASCII字符替换为连续问号。原验收器只检查UTF-8／BOM／键集合／token，因此对语法有效但无法展示正确内容的文件返回PASS。原始问题报告保留在调用项目的1015-candidate-open-kaishek-v7.json，不能将其认定为有效本地化验收。

在read_localization中补上两个有界错误：值含Unicode替代字符U+FFFD，或四个及以上连续ASCII问号时，报LOCALIZATION_CHARACTER_LOSS，包含文件、键、行号和reason。拒绝已知编码损坏的明显形式，保留正常问句和孤立问号。不据此宣称完成语义翻译审核或覆盖所有乱码形式。所有语言均适用，不修改游戏或调用方Mod文件。

## 验收合同

使用真实UTF-8 BOM小文件调用现有读取函数，证明三种损坏形式（纯连续问号、嵌入长问号段、U+FFFD）返回精确错误并保留键值用于后续完整报告；正常中文和英文单问号不报损坏。运行既有验收器完整单元测试。修复后用原损坏生产包产生FAIL新报告，再由调用项目修复本地化并生成新的PASS，不得覆盖原先的错误PASS。文档与工具提交并推送后恢复Mod验收。

## 实际结果

- `py -m unittest tools.test_accept_stellaris_mod -v`：15项通过，包括新增正／反例。
- `py tools/run_static_acceptance.py`：全仓库离线检查PASS，Maven重建与各模块／CLI回归通过；未运行CK3或外部语料。
- 未修正的调用方生产包实际重查为FAIL，精确拒绝三项中文母舰值以及先前缓存中一个九问号日文值。对应新报告为1015-candidate-open-kaishek-v7-character-loss-fail.json，原错误PASS未覆盖。调用方还检查并修复德文变音字母的孤立问号损坏；后者不在本有界规则的自动识别范围内。
