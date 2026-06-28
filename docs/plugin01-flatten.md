

###### ==*flatten 到底是什么*==？

这是大家最容易误解的地方。先说一句话：**flatten 和 `${revision}` 没有直接关系**。

举个例子。你的 Parent：

```xml
<version>${revision}</version>

<properties>
    <revision>1.0.0</revision>
</properties>
```

1. Maven **完全支持**，甚至 `mvn package` 都能成功。
2. 所以，没有 flatten，`${revision}` 照样可以工作。

------

###### ==*flatten 到底干什么*==？

举个例子。你的 Parent：

```xml
<version>${revision}</version>
```

module：

```xml
<parent>
	<version>${revision}</version>
</parent>
```

1. 如果，直接 `mvn install` 上传到 Nexus。
2. 别人下载，Jar 里面 POM 还是 `<version>${revision}</version>`。
3. 别人，根本不知道 `revision` 是多少，因为 properties 已经不存在了。

于是：依赖解析失败。

------

flatten 做的事情就是：

1. 发布前，自动改成：`<version>1.0.0</version>`.
2. Parent 也改 `<version>1.0.0</version>`.
3. Dependency 全部展开。

最后，上传的是，真正可消费的 POM。

------

所以，flatten 不是为了 ==***编译***==。而是，为了 ==**发布**==。

------

###### ==为什么 Spring 官方几乎不用 flatten==？

因为，Spring 不用 revision。

他们直接 `<version>6.2.0</version>`；所以，没有 `CI Friendly Version` 问题。

------

###### ==你要不要用 flatten==？

我觉得：**要**。

原因：你已经决定以后统一 `revision`。

例如：`<revision>1.0.0-SNAPSHOT</revision>`。

以后：改版本只改一个地方。这是非常舒服的。那么发布一定要：flatten。

否则：别人拿到 POM，就是 `${revision}` 无法解析。

------

