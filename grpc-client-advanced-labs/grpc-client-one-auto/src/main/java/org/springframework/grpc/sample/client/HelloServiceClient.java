package org.springframework.grpc.sample.client;

import io.github.rocwg.grpc.contract.hello.v1.HelloServiceGrpc;
import io.github.rocwg.grpc.contract.hello.v1.SayHelloRequest;
import io.github.rocwg.grpc.contract.hello.v1.SayHelloResponse;
import io.grpc.StatusRuntimeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
public class HelloServiceClient {

    private static final Logger log = LoggerFactory.getLogger(HelloServiceClient.class);

    // 构造器注入
    private final HelloServiceGrpc.HelloServiceBlockingStub helloServiceStub;

    /**
     * 【精妙之处】
     * 1. 这里的 Stub Bean 是由 @ImportGrpcClients 自动帮我们注册进容器的，完全没有浪费官方便利！
     * 2. 采用构造器注入，在编写单元测试时，你可以极其简单地通过 new HelloServiceClient(mockStub) 进行 Mock 测试。
     */
    public HelloServiceClient(HelloServiceGrpc.HelloServiceBlockingStub helloServiceStub) {
        this.helloServiceStub = helloServiceStub;
    }

    /**
     * 封装对外的业务方法
     */
    public String invokeSayHello(String name) {
        try {
            SayHelloRequest request = SayHelloRequest.newBuilder().setName(name).build();
            SayHelloResponse response = helloServiceStub.sayHello(request);
            return response.getMessage();
        } catch (StatusRuntimeException e) {
            log.error("调用 HelloService gRPC 异常, 状态码: {}", e.getStatus().getCode());
            // 在这里可以转换成你项目的自定义业务异常，例如 throw new BusinessException("远程调用失败");
            throw new RuntimeException("服务暂不可用，请稍后再试", e);
        }
    }
}
