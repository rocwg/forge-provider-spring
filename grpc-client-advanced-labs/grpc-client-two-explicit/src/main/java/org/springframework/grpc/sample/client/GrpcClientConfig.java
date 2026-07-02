package org.springframework.grpc.sample.client;

import io.github.rocwg.contract.iam.v1.AuthenticationServiceGrpc;
import io.github.rocwg.grpc.contract.hello.v1.HelloServiceGrpc;
import io.grpc.ManagedChannel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.grpc.client.GrpcChannelFactory;

/**
 * 【完美】配置类(启动类)不需要挂载任何不确定的 @ImportGrpcClients 了
 * 【完美】不需要任何 @SuppressWarnings 补丁，IDEA 能够 100% 静态识别这两个 Bean！
 */
@Configuration
public class GrpcClientConfig {

    /**
     * 【精妙点】我们不使用 @ImportGrpcClients 的全自动大锅饭，而是通过官方核心工厂手动接管。
     * 这依然享受了“框架帮我们管理连接池和负载均衡”的底座便利，但代码显式暴露为 Bean！
     */
    @Bean
    public HelloServiceGrpc.HelloServiceBlockingStub helloServiceBlockingStub(GrpcChannelFactory channelFactory) {
        // "hello-service" 对应 yaml 中的逻辑通道名
        return HelloServiceGrpc.newBlockingStub(channelFactory.createChannel("hello-service"));
    }

    @Bean
    public AuthenticationServiceGrpc.AuthenticationServiceBlockingStub authenticationServiceBlockingStub(GrpcChannelFactory channelFactory) {
        // "authentication-service" 对应 yaml 中的逻辑通道名
        ManagedChannel channel = channelFactory.createChannel("authentication-service");
        return AuthenticationServiceGrpc.newBlockingStub(channel);
    }
}
