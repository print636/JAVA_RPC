# 🚀 JAVA_RPC Framework

## 📖 项目简介

**RPC** 全称是 **Remote Procedure Call**（远程过程调用）。简单来说，就是让程序可以像调用本地方法一样，调用其他机器上的方法。

想象一下，你在家里想吃外卖，不用自己做饭，只需要打电话给餐厅点餐，然后等外卖送到家。这个"打电话点餐"的过程，就像是RPC！

这个项目是一个完整的RPC框架实现，包含了分布式系统中的核心功能：服务注册发现、负载均衡、容错处理、链路追踪等。

## ✨ 项目特色

### 🎯 核心功能
- ✅ **服务注册发现** - 自动发现可用的服务
- ✅ **负载均衡** - 智能分配请求到不同服务器
- ✅ **容错处理** - 自动重试、熔断保护
- ✅ **多种序列化** - 支持JSON、Hessian、Kryo等多种格式
- ✅ **心跳检测** - 自动检测服务是否正常
- ✅ **分布式追踪** - 跟踪请求在系统中的流转

### 🏗️ 架构设计
```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   Consumer      │────│   Registry      │────│   Provider      │
│   (客户端)      │    │   (注册中心)    │    │   (服务端)      │
│                 │    │   ZooKeeper     │    │                 │
└─────────────────┘    └─────────────────┘    └─────────────────┘
         │                       │                       │
         └───────────────────────┼───────────────────────┘
                                 │
                    ┌─────────────────┐
                    │   Load Balance │
                    │   (负载均衡)   │
                    └─────────────────┘
```

## 📁 项目结构

```
java-rpc/
├── krpc-api/          # 接口定义模块
│   ├── UserService.java     # 用户服务接口
│   └── User.java           # 用户数据模型
├── krpc-common/       # 公共工具模块
│   ├── serializer/         # 序列化器
│   ├── message/           # 消息协议
│   └── util/              # 工具类
├── krpc-core/         # 核心功能模块
│   ├── client/            # 客户端实现
│   ├── server/            # 服务端实现
│   └── servicecenter/     # 服务中心
├── krpc-consumer/    # 客户端应用
│   └── ConsumerTest.java  # 客户端测试
└── krpc-provider/    # 服务端应用
    └── ProviderTest.java  # 服务端测试
```

## 🛠️ 环境要求

### 必备软件
- **JDK 17+** - Java开发环境
- **Maven 3.6+** - 项目构建工具
- **ZooKeeper 3.9+** - 服务注册中心

### 推荐配置
- **内存**: 至少2GB可用内存
- **磁盘**: 至少1GB可用空间
- **网络**: 确保2182端口（ZooKeeper）和9999端口（RPC服务）可用

## 🚀 快速开始

### 第一步：安装ZooKeeper

1. **下载ZooKeeper**
   ```bash
   # 下载ZooKeeper（选择一个镜像站点）
   wget https://dlcdn.apache.org/zookeeper/zookeeper-3.9.4/apache-zookeeper-3.9.4-bin.tar.gz
   # 或者
   wget https://archive.apache.org/dist/zookeeper/zookeeper-3.9.4/apache-zookeeper-3.9.4-bin.tar.gz
   ```

2. **解压安装**
   ```bash
   # 解压到D盘（Windows）
   tar -xzf apache-zookeeper-3.9.4-bin.tar.gz
   move apache-zookeeper-3.9.4-bin D:\Tools\apache-zookeeper-3.9.4-bin
   ```

3. **配置ZooKeeper**
   编辑 `D:\Tools\apache-zookeeper-3.9.4-bin\conf\zoo.cfg` 文件：
   ```properties
   # 数据目录
   dataDir=D:/Tools/apache-zookeeper-3.9.4-bin/data

   # 客户端端口（默认2181，我们改为2182）
   clientPort=2182

   # 禁用管理服务器（避免端口冲突）
   admin.enableServer=false
   ```

4. **启动ZooKeeper**
   ```bash
   # Windows
   D:\Tools\apache-zookeeper-3.9.4-bin\bin\zkServer.cmd

   # 验证启动成功
   D:\Tools\apache-zookeeper-3.9.4-bin\bin\zkCli.cmd -server 127.0.0.1:2182
   ```

### 第二步：下载和编译项目

1. **克隆项目**
   ```bash
   cd D:\Code\IdeaProjects
   git clone https://github.com/print636/java_rpc.git
   cd java_rpc
   ```

2. **编译项目**
   ```bash
   # 编译所有模块
   mvn clean install -DskipTests
   ```

### 第三步：运行演示

1. **启动服务端**
   ```bash
   cd krpc-provider
   mvn spring-boot:run -Dspring-boot.run.main-class=com.kama.provider.ProviderTest
   ```

2. **启动客户端**
   ```bash
   cd krpc-consumer
   mvn spring-boot:run -Dspring-boot.run.main-class=com.kama.consumer.ConsumerTest
   ```

3. **观察结果**
   客户端会自动连接ZooKeeper，调用服务端的方法，并显示结果。

## 🔍 核心技术详解

### 1️⃣ 什么是RPC？

**RPC = Remote Procedure Call（远程过程调用）**

简单比喻：
- **本地调用**：你在家做饭（所有材料都在厨房）
- **RPC调用**：你打电话给餐厅点餐（厨师在餐厅，你在家里）

**传统HTTP调用** vs **RPC调用**：

| 对比项 | HTTP调用 | RPC调用 |
|-------|---------|--------|
| **协议** | HTTP协议 | 自定义二进制协议 |
| **效率** | 包含大量HTTP头信息 | 只传输必要数据 |
| **易用性** | 需要手动处理请求响应 | 像调用本地方法一样简单 |

### 2️⃣ 服务注册发现

**ZooKeeper就像一个"服务黄页"**

```
ZooKeeper目录结构：
/MyRPC/
├── com.kama.service.UserService/     # 服务目录（持久节点）
│   ├── localhost:9999               # 服务实例（临时节点）
│   └── localhost:9998               # 另一个实例
└── CanRetry/                        # 可重试方法目录
    └── localhost:9999/
        ├── getUserByUserId          # 可重试的方法
        └── insertUserId
```

**节点类型说明**：
- **持久节点**：服务目录，一直存在
- **临时节点**：服务实例，服务断开自动删除

### 3️⃣ 负载均衡

**一致性哈希算法 - 像钟表指针一样分配请求**

```
哈希环示意图：
   真实节点A (hash: 100)
          │
   虚拟节点A&&VN0 (hash: 50)  ─── 映射到 ──► 真实节点A
          │
   虚拟节点A&&VN1 (hash: 150) ─── 映射到 ──► 真实节点A
          │
   真实节点B (hash: 200)
          │
   虚拟节点B&&VN0 (hash: 180) ─── 映射到 ──► 真实节点B

请求"user123" → hash计算 → 找到最近的虚拟节点 → 返回真实节点
```

### 4️⃣ 容错处理

**熔断器模式 - 像保险丝保护电路**

```
正常状态 ──► 允许请求通过
    │
    ▼
请求失败 ──► 计数器+1
    │
    ▼
失败率过高 ──► 熔断器打开（拒绝请求）
    │
    ▼
等待恢复 ──► 熔断器半开（尝试少量请求）
    │
    ▼
请求成功 ──► 熔断器关闭（恢复正常）
```

### 5️⃣ 序列化

**把对象变成字节流，便于网络传输**

支持的序列化方式：
- **JDK原生**：Java内置序列化
- **JSON**：人类可读，兼容性好
- **Hessian**：二进制格式，性能高
- **Kryo**：高性能序列化
- **Protostuff**：基于Protobuf的无模式序列化

## 🐛 故障排除

### 常见问题

#### 1. ZooKeeper连接失败
```
错误：Connection refused: connect
解决：
1. 检查ZooKeeper是否启动
2. 检查端口2182是否被占用
3. 检查防火墙设置
```

#### 2. 服务注册失败
```
错误：服务注册失败
解决：
1. 检查ZooKeeper连接
2. 检查端口9999是否被占用
3. 查看日志中的详细错误信息
```

#### 3. 客户端调用失败
```
错误：executor not accepting a task
解决：
这是正常现象，表示系统在高并发下启动了保护机制
减少线程池大小或增加请求间隔即可
```

#### 4. 编译失败
```
错误：找不到依赖
解决：
1. 运行 mvn clean install
2. 检查网络连接
3. 清除本地仓库缓存
```

### 调试技巧

1. **查看ZooKeeper状态**
   ```bash
   # 连接ZooKeeper客户端
   zkCli.cmd -server 127.0.0.1:2182

   # 查看服务注册情况
   ls /MyRPC
   ls /MyRPC/com.kama.service.UserService
   ```

2. **查看应用日志**
   - 服务端日志：krpc-provider控制台输出
   - 客户端日志：krpc-consumer控制台输出

3. **网络连通性测试**
   ```bash
   # 测试ZooKeeper端口
   telnet 127.0.0.1 2182

   # 测试RPC服务端口
   telnet 127.0.0.1 9999
   ```

## 📚 学习路径

### 入门级（推荐新手）
1. 运行项目，观察基本功能
2. 阅读 ConsumerTest.java，理解客户端调用
3. 阅读 ProviderTest.java，理解服务端实现

### 中级进阶
1. 学习序列化机制
2. 理解负载均衡算法
3. 掌握ZooKeeper的使用

### 高级深入
1. 自定义序列化器
2. 实现新的负载均衡算法
3. 添加新的容错策略

## 🤝 贡献指南

欢迎提交Issue和Pull Request！

### 提交代码流程
1. Fork本项目
2. 创建特性分支：`git checkout -b feature/AmazingFeature`
3. 提交更改：`git commit -m 'Add some AmazingFeature'`
4. 推送到分支：`git push origin feature/AmazingFeature`
5. 创建Pull Request

### 代码规范
- 使用Java 17语法
- 遵循阿里巴巴Java开发规范
- 添加必要的注释和文档
- 提交前运行测试：`mvn test`

## 📄 许可证

本项目采用MIT许可证 - 查看 [LICENSE](LICENSE) 文件了解详情。

## 🙋‍♂️ 常见问题

**Q: 这个项目适合新手学习吗？**
A: 非常适合！项目包含详细的注释和文档，从零开始也能理解。

**Q: 我需要什么基础知识？**
A: 只需要掌握Java基础语法，网络编程和分布式概念会边学边懂。

**Q: 项目支持哪些操作系统？**
A: 支持Windows、Linux、macOS，只要安装了JDK和Maven即可运行。

**Q: 可以用于生产环境吗？**
A: 这是一个学习项目，生产环境建议使用成熟的RPC框架如Dubbo、gRPC等。

---

**Happy Coding! 🎉**

有问题欢迎在GitHub Issues中提问！