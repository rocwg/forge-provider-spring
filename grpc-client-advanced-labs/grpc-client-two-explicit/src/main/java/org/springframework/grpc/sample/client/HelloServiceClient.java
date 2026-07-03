package org.springframework.grpc.sample.client;

import io.github.rocwg.grpc.contracts.hello.v1.HelloServiceGrpc;
import io.github.rocwg.grpc.contracts.hello.v1.SayHelloRequest;
import io.github.rocwg.grpc.contracts.hello.v1.SayHelloResponse;
import io.grpc.StatusRuntimeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class HelloServiceClient {

    private static final Logger log = LoggerFactory.getLogger(HelloServiceClient.class);

    // 构造器注入
    private final HelloServiceGrpc.HelloServiceBlockingStub helloServiceBlockingStub;

    public HelloServiceClient(HelloServiceGrpc.HelloServiceBlockingStub helloServiceBlockingStub) {
        this.helloServiceBlockingStub = helloServiceBlockingStub;
    }

    /**
     * 封装对外的业务方法
     */
    public String invokeSayHello(String name) {
        try {
            SayHelloRequest request = SayHelloRequest.newBuilder().setName(name).build();
            SayHelloResponse response = helloServiceBlockingStub.sayHello(request);
            return response.getMessage();
        } catch (StatusRuntimeException e) {
            log.error("调用 HelloService gRPC 异常, 状态码: {}", e.getStatus().getCode());
            throw new RuntimeException("服务暂不可用，请稍后再试", e);
        }
    }

    /**
     * （4）如何细致地查看 Stub 真正走的是哪个 channel
     * 在调试或生产排查中，想确切知道 Stub 在运行时到底绑定了哪一个具体的物理连接或逻辑通道，通常有以下 3 种极具实操性的方法：
     * <p>
     * 方案一：通过反射（Reflection）在代码运行时打印（适合调试）
     * gRPC 的 Stub 内部持有一个叫 getChannel() 的对象，通过将其强转或反射，可以窥探其连接的真实 Target。
     * <p>
     * 你可以将这一段代码加在你的 CommandLineRunner 或者初始化方法中执行：
     */
//    public void debugStubChannel() {
//        Channel channel = helloServiceStub.getChannel();
//        // 打印其类名和 Hash，如果是 Spring 代理或官方托管的，能看到对应的具体实现
//        System.out.println("Stub 类名: " + helloServiceStub.getClass().getSimpleName());
//        System.out.println("绑定的 Channel 实现类: " + channel.getClass().getName());
//        System.out.println("Channel 字符串详情 (通常包含 target 信息): " + channel.toString());
//    }
}
