# Stellaris 实体资产脚本覆盖

## 目标与范围（实施前）

吞噬之心新增主宰外观的原生entity.clone别名，需要gfx/models下的.asset脚本。现有Mod包检查只发现txt/gfx/gui/descriptor，无法给该脚本提供P语法、UTF-8／无BOM和资源引用检查。依调用方仓库规则，先完善工具，再继续Mod验收。

将.asset以大小写不敏感方式加入现有P文件发现集合，沿用Kaishek parse／roundTrip、BOM和路径检查。不是解析.mesh等二进制，也不声明认证entity.clone目标存在或实际模型可见；模型与移动船坞仍须由游戏实机检查。

## 验收合同

合格最小包内加入有效entity.clone的.ASSET后，p_scripts与p_parsed增加1；畸形.asset精确报告P_SCRIPT_PARSE，BOM报告P_SCRIPT_BOM。运行完整包检查单元测试和全仓库离线static acceptance，检查日志与文档后，仅提交本工具任务文件并推送。原调用方已保存的v8～v10失败资产／行为证据不覆盖。

## 结果

`py -m unittest tools.test_accept_stellaris_mod -v`共17项通过，包含新增大写.ASSET发现与畸形／BOM反例；`py tools/run_static_acceptance.py`完整离线重建和各模块／CLI／包检查回归PASS，最后再次运行17项包检查。未运行CK3或外部语料。entity.clone命中目标及实际外观仍由调用方游戏负责。
