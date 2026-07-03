package org.springframework.grpc.sample.client;

import io.github.rocwg.grpc.contracts.iam.v1.AuthenticationServiceGrpc;
import io.github.rocwg.grpc.contracts.iam.v1.LoginRequest;
import io.github.rocwg.grpc.contracts.iam.v1.LoginResponse;
import io.grpc.StatusRuntimeException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuthenticationServiceClient {

    private static final Logger log = LoggerFactory.getLogger(AuthenticationServiceClient.class);

    // 纯粹的构造器注入
    private final AuthenticationServiceGrpc.AuthenticationServiceBlockingStub authenticationServiceBlockingStub;

    /**
     * 封装对外的业务方法
     */
    public String invokeLogin(String name) {
        try {
            LoginRequest request = LoginRequest.newBuilder().setAccount(name).setPassword("123456").build();
            LoginResponse login = authenticationServiceBlockingStub.login(request);
            return login.getProfile().getNickname();
        } catch (StatusRuntimeException e) {
            log.error("调用 AuthenticationService gRPC 异常, 状态码: {}", e.getStatus().getCode());
            throw new RuntimeException("服务暂不可用，请稍后再试", e);
        }
    }
}
