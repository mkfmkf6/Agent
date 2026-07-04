<<<<<<< HEAD
# vue-agent

This template should help get you started developing with Vue 3 in Vite.

## Recommended IDE Setup

[VS Code](https://code.visualstudio.com/) + [Vue (Official)](https://marketplace.visualstudio.com/items?itemName=Vue.volar) (and disable Vetur).

## Recommended Browser Setup

- Chromium-based browsers (Chrome, Edge, Brave, etc.):
  - [Vue.js devtools](https://chromewebstore.google.com/detail/vuejs-devtools/nhdogjmejiglipccpnnnanhbledajbpd)
  - [Turn on Custom Object Formatter in Chrome DevTools](http://bit.ly/object-formatters)
- Firefox:
  - [Vue.js devtools](https://addons.mozilla.org/en-US/firefox/addon/vue-js-devtools/)
  - [Turn on Custom Object Formatter in Firefox DevTools](https://fxdx.dev/firefox-devtools-custom-object-formatters/)

## Type Support for `.vue` Imports in TS

TypeScript cannot handle type information for `.vue` imports by default, so we replace the `tsc` CLI with `vue-tsc` for type checking. In editors, we need [Volar](https://marketplace.visualstudio.com/items?itemName=Vue.volar) to make the TypeScript language service aware of `.vue` types.

## Customize configuration

See [Vite Configuration Reference](https://vite.dev/config/).

## Project Setup

```sh
npm install
```

### Compile and Hot-Reload for Development

```sh
npm run dev
```

### Type-Check, Compile and Minify for Production

```sh
npm run build
```
=======
# Agent
独立开发的企业 HR 数字员工，架构：前端层（Vue3）：提供“聊天对话窗”+“数据看板/查询模拟器”。 后端层（SpringBoot）：与前端交互，远程调用智能体，为智能体远程提供工具     智能体层（LangChain）：负责意图识别与工具路由。     数据服务层：         关系数据库（结构化数据）：存储员工档案、考勤记录、工资明细等表格数据。         本地知识库（非结构化数据）：存储《员工手册》、《考勤管理制度》、《个税计算规则》等PDF/Word文档，通过向量检索（Chroma/Faiss）提供制度依据。
>>>>>>> c2dda1275d70c6c38b086d27526d97d8a998b54a
