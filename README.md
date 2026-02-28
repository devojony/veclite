# Veclite

Veclite 是一个 Android 平台的向量数据库和 LLM 集成示例项目，展示了如何在移动端实现向量搜索和本地 LLM 推理。

## 功能特性

### 1. 向量数据库 (Vector Database)
- 基于 SQLite 的向量存储和检索
- 支持多种相似度搜索算法（L2 距离等）
- 集成 Ollama API 进行文本向量化
- 实时向量搜索和文档管理

### 2. LLM 本地推理 (LLama.cpp)
- 集成 llama.cpp 实现本地 LLM 推理
- 支持多种模型（Phi3 等）
- 流式对话响应
- 完整的聊天界面

## 项目结构

```
veclite/
├── app/                    # 主应用模块
│   └── src/main/java/com/devo/veclite/
│       ├── llama/         # LLM 相关功能
│       ├── vector/        # 向量数据库功能
│       ├── entity/        # 数据实体
│       └── ui/            # UI 主题
├── sqlite3/               # SQLite 核心库
├── sqlite-ext/            # SQLite 向量扩展
└── llama-bindings/        # LLama.cpp Android 绑定
```

## 技术栈

- **语言**: Kotlin
- **UI**: Jetpack Compose
- **数据库**: SQLite with vector extensions
- **网络**: Ktor Client
- **LLM**: llama.cpp
- **架构**: MVVM

## 依赖项

- Android SDK 24+
- Kotlin 1.9.0
- Jetpack Compose
- Ktor 2.3.12
- llama.cpp (通过 JNI 绑定)

## 安装和使用

### 前置要求

- Android Studio Arctic Fox 或更高版本
- JDK 8 或更高版本
- Android SDK API 24+

### 构建步骤

1. 克隆仓库
```bash
git clone https://github.com/yourusername/veclite.git
cd veclite
```

2. 使用 Android Studio 打开项目

3. 同步 Gradle 依赖

4. 运行项目到模拟器或真机

### 使用 LLM 功能

1. 下载支持的模型文件（如 Phi3）
2. 将模型文件放置到设备的 `/sdcard/llama/` 目录
3. 在应用中点击 "LLama.cpp Demo" 加载模型
4. 开始对话

### 使用向量数据库功能

1. 点击 "向量数据库Demo"
2. 添加文本内容到数据库
3. 使用搜索功能查找相似内容
4. 支持切换不同的搜索算法

## 配置

### Ollama API

向量化功能需要 Ollama API。在 `Ollama.kt` 中配置您的 API 端点：

```kotlin
// app/src/main/java/com/devo/veclite/util/Ollama.kt
// 配置您的 Ollama API 地址
```

## 贡献

欢迎贡献！请查看 [CONTRIBUTING.md](CONTRIBUTING.md) 了解详细信息。

## 许可证

本项目采用 MIT 许可证 - 查看 [LICENSE](LICENSE) 文件了解详情。

## 致谢

- [llama.cpp](https://github.com/ggerganov/llama.cpp) - 高效的 LLM 推理引擎
- [SQLite](https://www.sqlite.org/) - 嵌入式数据库
- Android 开源社区

## 联系方式

如有问题或建议，请提交 Issue 或 Pull Request。
