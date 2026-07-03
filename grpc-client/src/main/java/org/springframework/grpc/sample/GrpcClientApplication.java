package org.springframework.grpc.sample;

import io.github.rocwg.grpc.contracts.hello.v1.HelloServiceGrpc;
import io.github.rocwg.grpc.contracts.hello.v1.SayHelloRequest;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.grpc.client.ImportGrpcClients;

@SpringBootApplication
@ImportGrpcClients
public class GrpcClientApplication {

    public static void main(String[] args) {
        SpringApplication.run(GrpcClientApplication.class, args);
    }

    @Bean
    public ManagedChannel channel() {
        return ManagedChannelBuilder
            .forAddress("localhost", 9090)
            .usePlaintext()
            .build();
    }

    @Bean
    public HelloServiceGrpc.HelloServiceBlockingStub stub(ManagedChannel channel) {
        return HelloServiceGrpc.newBlockingStub(channel);
    }

    @Bean
    public CommandLineRunner runner(
        HelloServiceGrpc.HelloServiceBlockingStub stub
    ) {
        return args -> {
            System.out.println(stub.sayHello(SayHelloRequest.newBuilder().setName("Alien").build()));
        };
    }

}
