package org.springframework.grpc.sample.client;

import io.github.rocwg.contract.iam.v1.AuthenticationServiceGrpc;
import io.github.rocwg.grpc.contract.hello.v1.HelloServiceGrpc;
import org.springframework.context.annotation.Configuration;
import org.springframework.grpc.client.ImportGrpcClients;

/**
 * 将 @ImportGrpcClients 统一回收到启动类或配置类
 *
 * <p>
 * 【优雅点】
 * （1）在这里统一声明系统中要用到的所有 gRPC 客户端存根，不写在 Component 上
 * （2）这里的属性指定你需要注册的 gRPC 客户端 Stub 类
 */
@ImportGrpcClients({
    HelloServiceGrpc.HelloServiceBlockingStub.class,
    AuthenticationServiceGrpc.AuthenticationServiceBlockingStub.class
})
@Configuration
public class GrpcClientConfig {


}

/*
 在 Spring 中，@ImportGrpcClients 是一个类级别的元注解（属于配置驱动型注解） 。
 它被设计用来放在 配置类（@Configuration） 或 启动类（@SpringBootApplication） 上的，而不是放在具体的业务组件（@Component）上 。
 虽然目前放在 @Component 上利用 Spring 的注解扫描也能生效，但它会多次触发 ImportBeanDefinitionRegistrar 机制，不是最标准的做法。
 */
