# Spring AI 全栈示例项目

> 基于 Spring AI 1.0.0 框架的完整企业级AI应用开发示例，集成DeepSeek大语言模型，展示现代AI应用的核心功能和最佳实践。

## 📋 项目概述

本项目是一个完整的Spring AI学习和实践项目，包含两个核心模块：

- **spring-ai-GA**: AI各种场景测试和核心功能实现
- **spring-ai-mcp**: MCP (Model Context Protocol) 服务端实现

## 🚀 技术栈

### 核心框架
- **Spring Boot 3.4.5** + **Java 17**
- **Spring AI 1.0.0** - 企业级AI应用框架
- **DeepSeek API** - 先进的大语言模型（支持推理模式）
- **Elasticsearch** - 向量数据库和全文检索
- **Lombok** - 简化Java开发

### 主要依赖
```xml
<!-- Spring AI 核心依赖 -->
<dependency>
    <groupId>org.springframework.ai</groupId>
    <artifactId>spring-ai-starter-model-deepseek</artifactId>
</dependency>

<!-- 对话记忆功能 -->
<dependency>
    <groupId>org.springframework.ai</groupId>
    <artifactId>spring-ai-autoconfigure-model-chat-memory</artifactId>
</dependency>

<!-- MCP客户端支持 -->
<dependency>
    <groupId>org.springframework.ai</groupId>
    <artifactId>spring-ai-starter-mcp-client-webflux</artifactId>
</dependency>

<!-- 向量存储 - Elasticsearch -->
<dependency>
    <groupId>org.springframework.ai</groupId>
    <artifactId>spring-ai-starter-vector-store-elasticsearch</artifactId>
</dependency>
```

## 🎯 Spring AI 核心特性实现

### 1. 🤖 大模型调用能力
- **同步对话**: 标准的问答交互
- **流式对话**: 实时响应流处理
- **深度思考模式**: 支持`deepseek-reasoner`模型的推理过程展示
- **参数调优**: 支持temperature、maxTokens等模型参数配置

**核心实现**:
```java
// 深度思考模式示例
DeepSeekChatOptions options = DeepSeekChatOptions.builder()
    .model("deepseek-reasoner").build();
Flux<ChatResponse> stream = chatModel.stream(prompt);
```

### 2. 📝 提示工程 (Prompt Engineering)
- **模板化提示词**: 支持参数化系统提示词 (`systemPrompt.st`)
- **角色定义**: 专业的银行APP投资经理顾问AI角色
- **动态参数注入**: 支持用户信息（姓名、年龄、性别）动态替换

**特色功能**:
```java
// 参数化提示词使用
String content = chatClient.prompt()
    .system(spec -> spec.param("name","沈yi").param("age","18").param("sex","男"))
    .user("你好").call().content();
```

### 3. 🧠 会话记忆功能
- **内存存储**: `InMemoryChatMemoryRepository`实现
- **窗口限制**: 最大10条消息记忆机制
- **会话隔离**: 支持不同conversationId的独立会话管理
- **自动配置**: 基于Spring Boot的自动配置机制

**技术实现**:
```java
@Bean
ChatMemory chatMemory(ChatMemoryRepository repository) {
    return MessageWindowChatMemory.builder()
        .maxMessages(10)
        .chatMemoryRepository(repository).build();
}
```

### 4. 🔍 RAG 检索增强生成
RAG（Retrieval-Augmented Generation）通过外部知识库增强AI生成结果的准确性。

**完整工作流程**:
1. **文档提取**: 使用`TextReader`读取文本文件
2. **文档分割**: `TokenTextSplitter`按Token智能切分
3. **向量存储**: 存储到Elasticsearch向量数据库
4. **相似性检索**: 检索前5个最相关文档片段
5. **答案生成**: 结合上下文生成准确回答

**核心服务**:
- `DataLoaderService`: ETL数据加载服务
- `RetrievalService`: 向量检索服务  
- `GenerationService`: 结合检索结果生成答案

### 5. 🛠️ TOOLS工具/Function Call调用
- **工具定义**: 使用`@Tool`注解定义业务工具
- **参数验证**: 严格的参数校验和错误处理
- **业务集成**: 退票、查票等具体业务逻辑实现

**示例实现**:
```java
@Tool(name = "nameAndNumberTools", description = "根据名字和预定号退票")
public String nameAndNumberTools(
    @ToolParam(description = "名字，真实人名（必填）") String name,
    @ToolParam(description = "预定号，不能包含英文") String number) {
    return "退票成功";
}
```

### 6. 🔗 MCP 模型上下文协议
- **协议支持**: 完整的MCP客户端实现
- **传输方式**: 支持SSE和Stdio两种传输方式
- **工具集成**: 外部MCP工具的动态调用

## 🏗️ 高级功能特性

### 智能拦截器 (Advisors)
**ReReadingAdvisors**: 重读策略拦截器
- 让LLM重新审视输入问题，提高理解准确性
- 基于`BaseAdvisor`的模板方法模式
- 适用于复杂推理任务的性能提升

### 智能类型转换
- **Boolean类型**: 用于意图判断和分支逻辑
- **POJO类型**: 结构化数据提取（如地址信息）
- **枚举类型**: 任务类型分类

### 多ChatClient配置
- **planningChatClient**: 票务助手（温度0.7，平衡准确性）
- **botChatClient**: 智能客服（温度1.2，更具创造性）

## 📁 项目结构

```
spring-ai-GA/
├── spring-ai-GA/                    # 主功能模块
│   ├── src/main/java/com/syd/ai/
│   │   ├── config/                  # 配置类
│   │   │   ├── ChatClientConfig.java      # ChatClient配置
│   │   │   └── AiTools.java              # AI工具定义
│   │   ├── controller/              # 控制器
│   │   │   ├── OpenAiController.java     # 基础对话接口
│   │   │   ├── ToolsController.java      # 工具调用接口
│   │   │   └── RagController.java        # RAG功能接口
│   │   ├── vector/                  # 向量检索
│   │   │   ├── DataLoaderService.java    # 数据加载
│   │   │   ├── RetrievalService.java     # 检索服务
│   │   │   └── GenerationService.java    # 生成服务
│   │   ├── advisor/                 # 拦截器
│   │   │   └── ReReadingAdvisors.java    # 重读策略
│   │   ├── chatmemory/              # 会话记忆
│   │   │   └── ChatMemoryAutoConfiguration.java
│   │   ├── entity/                  # 实体类
│   │   │   ├── Address.java             # 地址信息
│   │   │   └── AiJob.java               # AI任务
│   │   └── enums/                   # 枚举类
│   │       └── MessageType.java         # 消息类型
│   ├── src/main/resources/
│   │   ├── application.yml          # 主配置文件
│   │   ├── files/systemPrompt.st    # 系统提示词模板
│   │   └── text-source.txt          # RAG测试数据
│   └── src/test/java/
│       └── SpringAiGaApplicationTests.java  # 丰富的测试用例
├── spring-ai-mcp/                   # MCP服务模块
│   └── [简化版实现，专注MCP协议]
└── README.md                        # 项目文档
```

## ⚙️ 配置说明

### DeepSeek模型配置
```yaml
spring:
  ai:
    deepseek:
      base-url: https://api.deepseek.com/v1
      api-key: ${DEEPSEEK_API_KEY}
      chat:
        options:
          model: deepseek-chat  # 支持deepseek-reasoner
          temperature: 0.7
          max-tokens: 2000
```

### Elasticsearch向量存储配置
```yaml
spring:
  elasticsearch:
    uris: http://127.0.0.1:9200
    username: elastic
    password: ${ES_PASSWORD}
  ai:
    vectorstore:
      elasticsearch:
        index-name: rag-index
        dimensions: 768
        initialize-schema: true
```

## 🚀 快速开始

### 1. 环境准备
- Java 17+
- Maven 3.6+
- Elasticsearch 8.x
- DeepSeek API Key

### 2. 配置环境变量
```bash
export DEEPSEEK_API_KEY=your_deepseek_api_key
export ES_PASSWORD=your_elasticsearch_password
```

### 3. 启动项目
```bash
# 启动主模块
cd spring-ai-GA
mvn spring-boot:run

# 启动MCP服务模块
cd spring-ai-mcp
mvn spring-boot:run
```

### 4. 测试接口
```bash
# 基础对话
curl -X POST http://localhost:8080/ai/chat \
  -H "Content-Type: application/json" \
  -d '{"message": "你好"}'

# RAG检索问答
curl -X POST http://localhost:8080/rag/ask \
  -H "Content-Type: application/json" \
  -d '{"question": "什么是Spring AI？"}'
```

## 📚 学习资源

- **SpringAI笔记**: [语雀文档](https://www.yuque.com/geren-t8lyq/ncgl94/nn41bs3n8svmgpbq?singleDoc#lgxSF)
- **教学视频**: [B站视频教程](https://www.bilibili.com/video/BV1MshAzQEdM?spm_id_from=333.788.videopod.sections&vd_source=d0389888fff2393e4d8678c9ec812237)
- **测试用例**: `SpringAiGaApplicationTests` 包含大量实用示例

## 🎯 项目亮点

1. **🏢 企业级架构**: 完整的Spring AI生态集成
2. **🧠 深度推理**: DeepSeek推理模式的完整支持
3. **🔍 生产级RAG**: 基于Elasticsearch的企业级向量检索
4. **🔗 前沿协议**: MCP模型上下文协议的完整实现
5. **🧪 丰富示例**: 283行测试用例覆盖所有功能点
6. **📦 模块化设计**: 清晰的功能模块划分和依赖管理
7. **🚀 生产就绪**: 完整的配置、错误处理和日志记录

## 📄 许可证

本项目仅供学习和研究使用。

