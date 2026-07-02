package org.springframework.grpc.sample;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.grpc.sample.client.HelloServiceClient;

@SpringBootApplication
@RequiredArgsConstructor
public class GrpcClientTwoApplication {

    public static void main(String[] args) {
        SpringApplication.run(GrpcClientTwoApplication.class, args);
    }

    private final HelloServiceClient helloServiceClient;
//    private final AuthenticationServiceClient authenticationServiceClient;

    @Bean
    public CommandLineRunner runner() {
        return args -> {

            // 调用封装后的安全客户端
            String message = helloServiceClient.invokeSayHello("Alien");
            System.out.println("收到响应 -> " + message);
//            helloServiceClient.debugStubChannel();

            // 调用封装后的 iam
//            String login = authenticationServiceClient.invokeLogin("Alien");
//            System.out.println("收到响应 -> " + login);
        };
    }
}
