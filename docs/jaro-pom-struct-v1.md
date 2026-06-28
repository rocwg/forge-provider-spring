POM 的模式

✅ **“根 POM = Parent + Aggregator”**。一个 POM 两个身份。

✅ Parent 与 Aggregator 分离。

通过图形化链条来理解 Maven 的**物理结构（磁盘目录）\**与\**逻辑结构（继承/聚合链）**，是彻底吃透底层工程学的最佳方式。以下是为你整理的模式 1 和模式 3 的深度解析与结构图解

------



## 📘 模式 1：单体应用骨架

> ==***Root = Parent + Aggregator***==：
> 这种模式常见于传统的单仓应用或早期的原型开发。根项目（Root）同时扮演了“一家之主（提供规范）”和“管家（聚合打包）”的双重角色。



1. 逻辑继承与聚合链

在逻辑上，所有的子模块直接认官方的 `spring-boot-starter-parent` 为“爷爷”，认本地的 `Root` 为“父亲”。

```ini
       [中央仓库 spring-boot-starter-parent]
                       ▲
                       │ (远程拉取规范)
      [本地 Root POM (Aggregator + Parent 职责合一)]
                       ▲
         ┌─────────────┴─────────────┐
         │ (本地继承)                 │ (本地继承)
   [grpc-server]               [grpc-client]
```



2. 物理目录结构

所有模块在物理上都在同一个标准层级下，极度扁平：

```ini
jaro-samples-grpc (Root 目录)
├── pom.xml          ⭐ Root POM (直接继承官方 Spring Boot，并用 <modules> 聚合子项)
├── grpc-server/
│   └── pom.xml      ⭐ 业务模块 (继承 Root)
└── grpc-client/
    └── pom.xml      ⭐ 业务模块 (继承 Root)
```



📝 架构师笔记

- **核心特征**：简单粗暴。整个项目只有一层自定义的 POM 关系。
- **致命痛点**：Root POM 职责较多。项目内的子模块（如 `common-utils`）与当前项目的 Root **强耦合**。如果公司其他项目想单独引入你的 `common-utils`，会连带把你这个项目特有的配置、打包插件全部继承过去，**无法做到跨项目、跨团队的纯净复用**。

---



## 📙 模式 3：链式多级继承

> **Parent 与 Aggregator 分离：链式多级继承**.
> 在一个独立的 Git 仓库内，完成“从基础设施规范（boot-parent）” 到 “项目业务组装（Root）” 再到 “具体执行单元（server/client）” 的**垂直套娃式控制**。

这是你目前正在使用的、极具工业感的单仓闭环分层脚手架。它通过在本地增加一个隔离的 `boot-parent` 目录，成功把“技术工程规范”和“业务项目协调”分在了两个不同的层级。

是**一套属于你的工程宪法**。

1. 目标不是：**最符合 Maven 教科书**。
2. 目标而是：**未来五年，新建一个 Spring Boot 多模块项目，只需要复制一套骨架，然后几乎不改 POM**。

对于这个目标来说，这套继承链反而非常契合：

- **官方默认**：最底层直接继承 `spring-boot-starter-parent`。
- **组织规范**：`boot-parent` 统一维护 `revision`、`flatten` 和组织级构建约定。
- **项目骨架**：Root 负责聚合模块和项目级公共配置。不要让 Root 承担太多 Parent 的职责。
- **业务模块**：只继承 Root，几乎不用写重复配置。



1. 逻辑继承与聚合链

这是一种**垂直的、长辈代际明确**的链式控制流。每个层级各司其职，互不越权：

```ini
       [中央仓库 spring-boot-starter-parent]
                       ▲
                       │ (远程拉取官方底座)
     [本地 boot-parent (⭐ 承载企业/项目技术硬规范)]
                       ▲
                       │ (通过 <relativePath> 物理继承)
     [本地 Root POM    (⭐ 纯工具人：负责当前项目模块的聚合与协调)]
                       ▲
         ┌─────────────┴─────────────┐
         │ (本地继承)                 │ (本地继承)
   [grpc-server]               [grpc-client]
```



2. 物理目录结构

在物理磁盘上，规范层（`boot-parent`）被内聚在项目根目录下一个特殊的子文件夹里：

```toml
jaro-samples-grpc (Root 目录)
├── boot-parent/
│   └── pom.xml      ⭐ 唯一技术 Parent (上承 Spring Boot 官方，下定公司/项目技术指标)
├── pom.xml          ⭐ Root POM (继承 boot-parent，同时用 <modules> 聚合真正的业务)
├── grpc-server/
│   └── pom.xml      ⭐ 业务模块 (继承 Root)
└── grpc-client/
    └── pom.xml      ⭐ 业务模块 (继承 Root)
```



📝 架构师笔记

- **核心特征**：==***职责垂直分离***==。

  - [ ] 🟢`boot-parent` 负责制定 Java 版本、GAV 命名空间、全局 `flatten` 插件；
  - [ ] 🟢`Root` 负责当好业务聚合器；业务模块只需对准 `Root`。
    

- **精妙之处**：在 Monorepo（单代码仓库）时代，这是 ==***开发体验最好、内聚度最高的黄金结构***==。

  - 对于目前规模的独立应用或单仓多模块来说，它把企业级规范（`boot-parent`）和当前业务完美地圈在了一个项目里。
  - 它不仅让 POM 的逻辑井然有序，还为未来升级到模式 2（分布式大厂多仓库）埋下了完美的伏笔——只要把 `boot-parent` 文件夹移走单独建仓发布，整个体系立刻无缝升级！
  - 既保证了技术规范的隔离，又不需要你跨 Git 仓库去维护。
    

- **实际构建生命周期**：因为 `root/pom.xml` 的 `modules` 里根本没有 `boot-parent`！

  1. **依赖查找阶段**：当你运行 `mvn clean package` 触发 `root` 时，Maven 看到 `root` 的 parent 是 `boot-parent`。
     它会顺着你的 `<relativePath>boot-parent/pom.xml</relativePath>` 找到这个物理文件并加载。
  2. **继承生效阶段**：`root` 成功继承了 `boot-parent` 中定义的官方 Spring Boot 父依赖、`${revision}`、以及非常关键的 `flatten-maven-plugin`。
  3. **业务聚合阶段**：`root` 开始并发或顺序编译它的子模块：`grpc-server` 和 `grpc-client`。这两个子模块又反向继承了 `root`。

  这形成了一条**绝对单向、没有环路的完美继承与聚合链条**：`Spring Boot Parent` ➡️ `boot-parent (企业/项目基础设施级)` ➡️ `Root (项目生命周期聚合级)` ➡️ `grpc-server / grpc-client (具体业务代码级)`。
  

- 🟢冻结职责

  | ① boot-parent/pom.xml（🔥 工程规范层）                        | ② Root pom.xml（🔥 项目协调层）                 | ③ grpc-server/pom.xml（🔥 业务模块标准） |
  | ------------------------------------------------------------ | ---------------------------------------------- | --------------------------------------- |
  | 继承 `spring-boot-starter-parent`（官方）                    | 继承  `boot-parent/pom.xml`。                  | 继承 Root。                             |
  | **Project 基本信息**（groupId、artifactId、version、packaging） |                                                |                                         |
  | ✔ properties：revision、encoding 等                          | ✔ modules：聚合模块（项目级）                  | ✔ dependencies：模块专属依赖。          |
  | ✔ dependencyManagement（非 Spring Boot 的部分）              | ✔ dependencyManagement：控制版本（项目级）     | ✔ build：Spring Boot plugin             |
  | ✔ build：flatten（少量公共插件/版本）                        | ✔ pluginManagement：统一 plugin 管理（不执行） |                                         |
  | 🌟 **profiles / 发布策略**（如果有）                          |                                                |                                         |
  | 🌟 repositories（如果有）                                     |                                                |                                         |
  | 🌟 distributionManagement（如果有）                           |                                                |                                         |


---



## 🔬 模式 2：分布式多仓库骨架

> 远程私服全权单向继承链

这是面向大厂、多团队、多业务线大规模协作时的**行业标准答案**。它将技术硬规范（`boot-parent`）彻底提拔为公司级公共基础设施，业务团队只需要关注自己的代码仓库。



1. 逻辑继承与聚合链

在逻辑上，它是一条干净、利落的**单向直线级联链条**。由于去掉了本地相对路径的羁绊，全盘依靠 Maven 仓库（私服）的网络拉取来进行解耦：

```ini
       [中央仓库 spring-boot-starter-parent]
                       ▲
                       │ (官方标准底座)
     [🏢 公司私服远程仓库 forestry-boot-parent] 
                       ▲
                       │ (通过网络拉取，彻底去掉了 <relativePath>)
     [💻 本地项目 Root POM (Aggregator + 项目内部协调层)]
                       ▲
         ┌─────────────┴─────────────┐
         │ (本地继承)                 │ (本地继承)
   [grpc-server]               [grpc-client]
```



2. 物理目录结构

此时，在你的业务 Git 仓库（`jaro-samples-grpc`）中，`boot-parent` 目录已经彻底看不见了，源码结构变得极度纯净、清爽：

```
jaro-samples-grpc (当前团队负责的独立业务 Git 仓库)
├── pom.xml          ⭐ Root POM (在 <parent> 标签中硬编码指向远程私服的坐标，不写 relativePath)
├── grpc-server/
│   └── pom.xml      ⭐ 业务服务端 (继承本地 Root)
└── grpc-client/
    └── pom.xml      ⭐ 业务客户端 (继承本地 Root)
```



📝 架构师笔记

- **核心特征**：**跨仓库共享与极极致解耦**。由架构组在一个名为 `forestry-boot-parent` 的**完全独立的 Git 仓库**中维护规范，测试通过后执行 `mvn deploy` 发布到公司私服（Nexus/Artifactory）。

- **核心优势**：

  1. **大一统**：全公司有 100 个业务仓库（林业仓、设备仓、财务仓），它们的 `Root POM` 都可以认私服里的同一个 `forestry-boot-parent` 当爹。某天架构组想统一升级某个安全漏洞依赖，只需要在私服发布 `2.0.0` 版本，各业务组修改 Root 的版本号即可。
  2. **组内自治**：本地的 `Root POM` 依然拥有 `<dependencyManagement>` 的控制权。如果林业组在开发 gRPC 时，需要引入一个只属于他们组的特定中间件（而这个中间件不需要全公司统一定义），组长可以直接写在本地 `Root POM` 里，既遵守了公司大规范，又保留了项目组的灵活性。

- POM 的关键变化：在根目录的 `pom.xml` 中，你只需要把它的 `<parent>` 指向私服坐标，并且**删掉 `<relativePath>` 这一行**：

  ```xml
  <parent>
      <groupId>io.github.rocwg</groupId>
      <artifactId>forestry-boot-parent</artifactId>
      <version>1.0.0</version>
  </parent>
  ```

  而你的 `grpc-server` 和 `grpc-client` **完全不需要做任何修改**，它们依然无脑继承 `Root` 即可。

---



## 🛠️ 终极对比备忘录（复习课）

在复习你的笔记时，可以这样一句话区分它们：

- **模式 1**：没有中间商，Root 自己既当爹（规范）又当妈（聚合），**适合单仓快速搞定**。
- **模式 3**：在本地生了个孩子叫 `boot-parent` 帮自己管家规，**适合单仓 Monorepo 垂直深度控制**。
- **模式 2**：把 `boot-parent` 送去全托（独立建仓发私服），本地 Root 只负责聚合和组内自治，**适合多仓库多团队的集团军作战**。

把这三张逻辑链路图和笔记收好，以后不管是面试、做技术分享，还是去新团队给新项目搭建微服务脚手架，你都能降维打击，从最底层的 Maven 生命周期和构建哲学上把团队说服！

---



总结：
**对于 95% 的 Java/Spring Boot 开发者和架构师来说，理解这三种模式就已经完全足够包揽你职业生涯中所有项目的脚手架设计了**。它们层层递进，因果关系极其严密。

| 三个阶段精准对应了软件开发的三个不同生命周期 | 评价                                                         |
| -------------------------------------------- | ------------------------------------------------------------ |
| 独立玩具/原型（模式1）                       | 单体：Root = Parent + Aggregator。（最简单，但 Root 职责稍多） |
| 团队独立核心项目（模式3）                    | **Spring Boot Parent → boot-parent → Root → Modules**（唯一的代价是继承链更深，需要理解四层关系） |
| 企业级分布式微服务集群（模式2）              | Parent 与 Aggregator 分离 + import BOM（工业界最标准、职责最清晰） |

---

- [ ] 坚持"职责隔离"的思想。
- [ ] 这套结构不会因为项目从 3 个模块增长到 20 个模块而需要重新设计，符合你一直追求的“先收敛，再长期稳定维护”的原则。
- [ ] 最后得到的不是一份“别人家的 POM”，而是**你完全理解、能够长期维护的 Jaro Parent V1**。这也最符合你一直坚持的“可控、可追溯、工程美感”的原则。

---

1

🧭 八、收敛后的“工业级最终结构”

```shell
jaro-samples-grpc
│
├── grpc-api-contract
│     ├── proto
│     └── published jar (1.0.0)
│
├── grpc-server
│     ├── adapter/grpc
│     ├── application
│     ├── domain
│     └── infrastructure
│
├── grpc-client
│     ├── sdk layer
│     └── facade layer
│
└── build pipeline (future CI)
```

🚀

```powershell
PowerShell 7.6.3
PS D:\roc-github> git clone git@github.com:rocwg/jaro-samples-grpc.git
PS D:\roc-github\jaro-samples-grpc> goro tree -depth=20
.
├── at-build
│   └── pom.xml
├── grpc-api-contract
│   ├── pom.xml
│   ├── src
│   │   └── main
│   │       └── proto
│   │           └── hello.proto
├── grpc-client
│   ├── pom.xml
│   └── src
├── grpc-server
│   ├── pom.xml
│   └── src
└── pom.xml
```

🚀

```powershell
jaro-grpc-system
│
├── contract/                # ⭐ API 契约层（唯一真相）
│   ├── proto/
│   ├── gen-java/
│   └── pom.xml
│
├── server/                  # ⭐ gRPC Provider（纯服务）
│   ├── bootstrap/
│   ├── adapter/grpc/
│   ├── application/
│   ├── domain/
│   ├── infrastructure/
│   └── pom.xml
│
├── client/                  # ⭐ SDK / Consumer
│   ├── sdk/
│   ├── facade/
│   └── pom.xml
│
├── build/                   # ⭐ 构建治理层（统一版本/插件）
│   ├── pom.xml
│   ├── dependency-management.xml
│   └── plugin-management.xml
│
└── pom.xml                  # aggregator
```

1
