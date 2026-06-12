# jaro-boot-dependencies

<p>
  <img src="https://img.shields.io/badge/Spring%20Boot-3.4.2-brightgreen.svg" alt="Spring Boot">
  <img src="https://img.shields.io/badge/JDK-21-blue.svg" alt="JDK">
  <img src="https://img.shields.io/badge/License-MIT-purple.svg" alt="License">
</p>

## 📝 简介
`jaro-boot-dependencies` 是一个专属于 **Jaro 技术生态** 的中央版本控制中心（BOM）。
本项目不包含任何业务逻辑代码，其核心职责是通过 Maven 的 `<dependencyManagement>` 机制，**集中、强力地收敛并管控全盘第三方组件的版本号**。

无论是在日常编写技术 Demo，还是高频交付企业微服务、业务项目，只需引入本项目作为外挂底层，即可实现“积木式”的快速起服，彻底告别依赖冲突。

---

## 📂 目录结构

```ini
jaro-boot-dependencies/
├── .gitignore
├── README.md
└── pom.xml
```

---

## 🛠️ 核心技术栈矩阵 (Version Matrix)

| 组件名称               | 依赖坐标 (ArtifactId)           | 控管版本  | 职责描述                       |
| :--------------------- | :------------------------------ | :-------- | :----------------------------- |
| **Spring Boot Parent** | `spring-boot-starter-parent`    | `3.4.2`   | 官方生态底层与默认编译插件规范 |
| **Sa-Token**           | `sa-token-spring-boot3-starter` | `1.45.0`  | 统一轻量级权限安全中心         |
| **MapStruct**          | `mapstruct`                     | `1.6.3`   | CRUD 极速属性映射/对象转换利器 |
| **Lombok**             | `lombok`                        | `1.18.36` | 基础效率工具                   |

---

## 🚀 快速上手实践

### 1. 本地发布底座
在克隆本项目到本地后，在根目录下执行以下命令，将底座发布至本地 Maven 仓库：
```bash
mvn clean install
```

### 2. 在业务项目中挂载

在业务多模块项目的**根目录 `pom.xml`** 中，通过 `scope=import` 一句话接入：

```xml
<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>io.github.rocwg</groupId>
            <artifactId>jaro-boot-dependencies</artifactId>
            <version>1.0.0-SNAPSHOT</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
    </dependencies>
</dependencyManagement>
```

### 3. 子模块“无版本号”丝滑开发

在业务子模块（如 `admin-service`）中引入依赖时，**彻底闭着眼睛写，绝不允许带 `<version>` 标签**：

```xml
<dependencies>
    <dependency>
        <groupId>cn.dev33</groupId>
        <artifactId>sa-token-spring-boot4-starter</artifactId>
    </dependency>
</dependencies>
```

## 🎯 长期维护与升级策略

1. **单点升级**：若某天需要升级第三方依赖（如升级 Sa-Token 修复漏洞），只需在此仓库的 `pom.xml` 的 `<properties>` 中修改对应的版本号数字。
2. **一处生效**：重新执行 `mvn clean install`。你手头所有挂载了此底座的业务项目，只需刷新 Maven 即可一秒完成底层技术栈的整体跃迁，绝不产生无谓的上下文切换成本。

---