# Contributing to open_kaishek / 为 open_kaishek 贡献

Thank you for contributing. This repository is an independently buildable JVM toolchain with explicit parser, validator, IR, runtime, profile, and evidence boundaries. Changes must preserve the contract and verification boundary of the component they touch.

感谢参与贡献。本仓库是可独立构建的 JVM 工具链，具有明确的 parser、validator、IR、runtime、profile 与证据边界；修改必须遵守所涉及组件的合同与验证边界。

## Before opening a pull request / 提交拉取请求前

1. Read the root README and the authoritative architecture or contract documentation linked for the component you intend to change.
2. Keep the change scoped. Do not mix unrelated modules, profiles, generated outputs, or evidence into the same pull request.
3. Do not hand-edit generated files; change their source and run the documented generator.
4. Run the smallest relevant Maven/Python checks and record the exact commands and results in the pull request.
5. Identify third-party code, data, fixtures, and generated material together with their license or authorization.

对应中文：先阅读根 README 与目标组件的权威架构/合同文档；保持改动单一；生成文件只能通过生成器更新；运行与风险相称的最小测试；第三方或生成材料必须说明来源、许可与必要署名。

## Contributor License Agreement / 贡献者许可协议

External contributions require acceptance of [CLA.md](CLA.md). The CLA is a
license, not a copyright assignment: contributors keep ownership of their work.

外部贡献必须接受 [CLA.md](CLA.md)。CLA 只授予许可，不转让贡献者的著作权。

The repository uses a small, repository-owned GitHub Actions checker. It has no
external service, database, GitHub App, personal access token, or secret beyond
the job-scoped `GITHUB_TOKEN`. Signatures are recorded as public pull-request
comments and therefore apply per pull request.

本仓库使用自有的轻量 GitHub Actions 检查器，不依赖外部服务、数据库、GitHub App、个人访问令牌或额外密钥。
签署记录就是公开的 PR 评论，因此按 PR 生效。

Every account reported by the checker must post this exact comment on the pull
request:

```text
I have read and agree to the Contributor License Agreement (CLA), version 1.2, and I confirm that I have authority to grant the rights for my contribution.
```

The checker covers the pull-request author and GitHub-linked commit authors. If
a commit author cannot be mapped to a GitHub account, the check stops for manual
review. Each additional contributor may sign by posting the same statement from
their own account.

检查器会覆盖 PR 作者以及能够关联到 GitHub 账号的提交作者。无法映射的提交作者会触发人工复核；多人共同贡献时，
每位被列出的贡献者都应使用自己的账号发表同一声明。

Maintainers may apply the `cla:manual` label only after separately verifying an
entity CLA, offline signature, unmapped author, or equivalent evidence. The
label is an auditable override, not a way for contributors to bypass the CLA.

维护者只有在核验企业 CLA、线下签名、无法映射的作者或等效证据后，才可添加 `cla:manual` 标签。该标签是可审计的人工覆盖，
不是贡献者绕过 CLA 的入口。

## Pull-request expectations / PR 要求

- Explain the user-visible or developer-visible outcome.
- Link the governing architecture, profile, or technical contract.
- List tests actually run; do not claim CK3/MCP live coverage from static or synthetic checks.
- Keep readiness levels, external-corpus facts, and live evidence honest and separate.
- Expect the required commit status `CLA / signed` before merge.

If you are contributing on behalf of an employer or another legal entity, make
sure you are authorized to bind it before signing. If that requires a separate
entity agreement, contact the maintainer before the contribution is merged.
