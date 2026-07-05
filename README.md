# 企业 HR 数字员工智能体
独立开发的企业 HR 数字员工，架构：前端层（Vue3）：提供“聊天对话窗”+“数据看板/查询模拟器”。 后端层（SpringBoot）：与前端交互，远程调用智能体，为智能体远程提供工具     智能体层（LangChain）：负责意图识别与工具路由。     数据服务层：         关系数据库（结构化数据）：存储员工档案、考勤记录、工资明细等表格数据。         本地知识库（非结构化数据）：存储《员工手册》、《考勤管理制度》、《个税计算规则》等PDF/Word文档，通过向量检索（Chroma/Faiss）提供制度依据。
   
基于 **SpringBoot + FastAPI + LangChain** 构建的企业级 HR 数字员工，通过双后端微服务架构实现 AI 智能体与业务系统解耦，支持招聘咨询、简历解析、考勤查询、薪酬答疑、员工信息维护等多个 HR 场景。

---

## 技术架构

- SpringBoot
- Vue3
- FastAPI
- LangChain
- MySQL
- Chroma
- Nacos

---

## 系统架构

```text
                Vue3
                  │
                  ▼
     SpringBoot（Chat API）
                  │
          Nacos 服务调用
                  │
                  ▼
      FastAPI（LangChain Agent）
                  │
       Intent Recognition
          Tool Calling
                  │
          Nacos 服务调用
                  │
                  ▼
 SpringBoot（Tool API / Business）
                  │
                Service
                  │
                MySQL

          Chroma（RAG知识库）
```

---

## 架构说明

本项目采用 **SpringBoot + FastAPI 双后端架构**。

### SpringBoot

负责整个业务系统，对外提供统一接口。

主要职责：

- 提供聊天接口（Chat API）
- 封装招聘、考勤、薪酬等 Tool 接口
- MySQL 数据访问
- 与前端(Vue3)交互

SpringBoot 不负责 Agent 推理，仅负责业务能力提供。

---

### FastAPI

FastAPI 仅负责 AI Agent 服务。

主要职责：

- LangChain Agent
- Prompt 管理
- 多意图识别
- Tool Calling
- 多轮对话
- RAG 检索

FastAPI 不直接访问数据库。

所有业务数据均通过 Tool 接口远程调用 SpringBoot 获取。

---

### Nacos

SpringBoot 与 FastAPI 均注册到 Nacos。

SpringBoot：

- 调用 Agent 服务

FastAPI：

- 调用 SpringBoot Tool 接口

整个调用过程无需硬编码 IP，通过服务发现完成远程调用。

---

## Agent 工作流程

```text
用户

↓

Vue3

↓

SpringBoot Chat API

↓

Nacos

↓

FastAPI Agent

↓

意图识别

↓

Tool Calling

↓

Nacos

↓

SpringBoot Tool API

↓

Service

↓

MySQL

↓

FastAPI

↓

SpringBoot

↓

Vue3
```

---

## 功能模块

- 招聘咨询
- 简历解析
- 入离职办理
- 考勤查询
- 薪酬答疑
- 员工信息维护
- 企业制度问答（RAG）

---

## Tool 设计

Agent 不直接访问数据库。

所有 Tool 均由 SpringBoot 封装，对外提供统一业务接口。

例如：

- RecruitmentTool
- AttendanceTool
- PayrollTool
- LeaveTool
- EmployeeTool

Agent 根据用户意图选择 Tool，并通过 Nacos 远程调用 SpringBoot 接口完成业务处理。

---

## RAG 知识库

采用 Chroma 构建企业知识库。

知识来源：

- 员工手册
- 考勤制度
- 请假制度
- 薪酬制度
- 个税计算规则

支持制度检索、政策问答及依据返回。

---

## 项目亮点

- SpringBoot + FastAPI 双后端架构
- Nacos 实现 AI 与业务服务解耦
- LangChain Agent 多意图识别与 Tool Calling
- SpringBoot 统一封装 Tool 接口，Agent 远程调用业务能力
- Chroma 构建企业知识库，实现 RAG 检索增强问答
