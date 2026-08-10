# 高校公寓管理系统

团队协作练手项目：Spring Boot + Vue3 前后端分离，覆盖用户认证、宿舍入住、报修管理三大核心模块。

远程仓库：`github` → https://github.com/Firefly-LiMou/dorm-ms-migration.git

## 技术栈

| 端 | 技术 |
| --- | --- |
| 后端 | Spring Boot 2.7.18 + MyBatis-Plus 3.5.5 + Sa-Token 1.37.x + Knife4j 4.3.0 |
| 前端 | Vue 3.3 + Vite 5 + Element Plus + Pinia + Vue Router + Axios |
| 数据库 | MySQL 8.0 |

## 目录结构

```
dorm-ms/
├── docs/                      # 需求分析、架构设计、接口清单文档
├── backend/                   # Spring Boot 后端工程（端口 8080，context-path /api）
├── frontend/                  # Vue3 前端工程（端口 5173）
├── scripts/                   # 数据库脚本：create.sql（建表）+ init-data.sql（种子数据）
├── .vscode/                   # 统一编辑器配置与推荐扩展
└── dorm-ms.code-workspace     # VSCode 工作区（用"文件→打开工作区"打开）
```

## 环境要求（全团队统一）

- JDK 1.8（若本机默认 JDK 为更高版本，需设置 `JAVA_HOME` 指向 JDK 1.8 后重启终端）
- Maven 3.9+（Windows 未安装可参考：解压到用户目录后加入用户 PATH；也可用 IDE 内置 Maven）
- MySQL 8.0
- Node.js 22 LTS
- VSCode（推荐，扩展见 `.vscode/extensions.json`，打开项目后按提示一键安装；用「文件 → 打开工作区」打开 `dorm-ms.code-workspace`）

## 本地启动步骤

1. **初始化数据库**：在 MySQL 中依次执行 `scripts/create.sql`、`scripts/init-data.sql`
2. **后端**：复制 `backend/src/main/resources/application-local.yml.example` 为 `application-local.yml`，填写本地数据库密码（该文件已被 gitignore，不会提交）；执行 `mvn spring-boot:run` 启动，接口文档访问 `http://localhost:8080/api/doc.html`
3. **前端**：`npm install && npm run dev` 启动，访问 `http://localhost:5173`（Vite 已将 /api 代理到后端 8080）

默认账号：管理员 `admin / 123456`，学生 `20240001 / 123456`、`20240002 / 123456`

## Git 协作规范（摘要，完整流程详见 docs/开发工作流.md）

- 分支：`main`（最终交付）+ `dev`（开发集成主干）双长期分支；`feature/*`、`bugfix/*` 临时分支（合并后删除）
- 流程：从 `dev` 切功能分支 → 开发完成 → 提 PR（测试+评审并行）→ Squash Merge 合入 `dev`（每日集成）→ 项目完成后合并 `main` 并打 `v1.0` 标签
- 提交：`type: 中文描述`（feat/fix/refactor/style/docs/chore/test），小步提交
- 强制：`main`/`dev` 禁止直推（GitHub 分支保护），PR 至少 1 人评审通过方可合并

## 文档索引

- 需求分析：`docs/高校公寓管理系统需求分析.md`
- 后端架构：`docs/后端架构设计.md` / 后端编码规范：`docs/后端编码规范.md`
- 前端架构：`docs/前端架构设计.md` / 前端编码规范：`docs/前端编码规范.md`
- 数据库设计：`docs/数据库表结构设计.md`
- 接口契约：`docs/2026-08-10-接口清单-核心接口v1.md`
- 开发规范：`docs/重点开发规范.md`
- 协作流程：`docs/开发工作流.md`
