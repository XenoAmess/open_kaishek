# ADR-0002：Phase 0（M0）开工与冻结门槛

**日期：** 2026-09-01
**状态：** 已批准开工；Phase 0/M0 静态正式评审已完成，readiness 为 `static-ready`；CK3 live 未提供

## 决定

项目所有者已授权独立仓库 `open_kaishek` 从前期预研进入 Phase 0 实施。当前已落地“合同冻结与工程决策”所需的文档、schema、构建工程和可复核的最小工具链；不把静态基线解释为 Runtime 或 CK3 实机已可用。

Phase 0 的完成门槛仍是 roadmap 中的 M0：

- 合同经过实例评审，能明确区分重复键、混合 block、scope、参数、事件、随机和 native port；
- 外部依赖均有许可证、固定版本、采用理由和 `THIRD_PARTY_LOCK` 记录；
- 核心模块无需 Quarkus 启动即可构建和测试；
- 第一批 corpus、CST/diagnostic/profile/IR/snapshot/trace/differential schema 可被审阅并可重复生成。

## 当前状态（2026-09-01）

| 项目 | 状态 | 说明 |
|---|---|---|
| Phase 0 / M0 | `static-ready` | 构建、许可证、manifest、profile/IR/runtime/diff 合同已落盘，并于 2026-09-01 完成本阶段正式静态评审；不代表 CK3 live。 |
| M1（lossless parser round-trip） | `static-ready`（冻结 corpus + bounded property/fuzz） | 27 个目标 `.txt/.gui` 文件逐字节 round-trip 通过；固定 seed 的 760-case property/fuzz 已通过，更大 mutation 矩阵仍待扩展。 |
| M2（schema validator） | `static-ready` | 小型 CK3 profile 与 361 schema validator fixture 通过；历史故障覆盖仍有限。 |
| M3（strict runtime synthetic 361） | `static-ready`（原语 + synthetic 014） | finite draw/stale/receipt/queue 合同与一个有限状态夹具通过；不代表 CK3 语义。 |
| M4/M5（exact-build / MCP differential） | `static-ready`（仅 synthetic 子集） / `not-available` | 014 夹具已走通生成脚本到 VM；完整 exact-build 范围和真实 paused artifact 仍缺失，认证集合为空。 |
| CK3 live / product-live | `not-available` | 没有 CK3 live artifact，不得写成 fixture-live 或 production-live。 |

## 冻结记录与剩余评审

以下是本次静态基线实际采用的版本；平台矩阵、CI 镜像和正式发布级 hash 复核属于
后续发布工作，不以默认值冒充已决策：

1. **JDK**：当前验证基线为 Eclipse Temurin `21.0.10+7`，源码/目标级别为 21；平台矩阵和发行版 hash 仍待评审。
2. **构建工具**：当前采用 Apache Maven `3.6.3+`，插件版本固定于父 POM，离线验证命令见 [Phase 0 验证记录](../phase0-verification.md)；CI 镜像与 wrapper 仍待决定。
3. **许可证**：项目采用 GNU GPLv3，SPDX 标识为 `GPL-3.0-only`，完整文本见仓库根
   `LICENSE`；核心无运行时第三方依赖，JUnit/构建插件的版本和 SPDX 信息已写入
   [`THIRD_PARTY_LOCK`](../../THIRD_PARTY_LOCK)。其中构建插件自身的 Apache-2.0
   许可证保持原样。Chronicle、CWT、PDXTools、Jomini 仍仅作研究输入，未复制源码。

上述平台与发布复核完成后，才能把静态基线升级为正式发布清单；当前构建文件只包含
已记录的测试/构建依赖。M0 的静态 readiness 已完成，M1 的冻结 corpus 与 bounded
property/fuzz 基线也已形成正式记录；更大 mutation 矩阵、完整 361 exact-build 范围、
MCP 差分和 CK3 paused artifact 仍是后续门槛。

## 本阶段允许与禁止

允许：编写并审阅 schema/接口草案、构建探针、最小 corpus 清单、许可证记录、静态
parser/validator 和 runtime 合同夹具。
禁止：声称完整 M1/M3/M4 端到端或 CK3 Runtime 已完成、执行或发布 CK3 Runtime、把
静态 fixture/ACK 当作 live、引入未经许可证审查的第三方代码，或为了“跑通”未知语义
而 silent no-op。synthetic 014 只作为离线夹具证据。

## 下一步与证据

M0 合同实例评审已经完成；Phase 1 的冻结 corpus、结构化语法基线和 bounded property/fuzz
覆盖已经收口；下一项可交付是更大 mutation 矩阵与扩展 B2/Workforce exact-build 范围的
361 纵向切片。当前 synthetic 014 已形成“生成脚本
→ parser → validator → IR → synthetic VM”闭环，但每项结果仍必须记录命令、版本、hash、
测试输出和未支持项；在真实 paused artifact 出现前，readiness 保持 `static-ready`，
不升级为 `fixture-live`、`differential-certified` 或 `production-live`。
