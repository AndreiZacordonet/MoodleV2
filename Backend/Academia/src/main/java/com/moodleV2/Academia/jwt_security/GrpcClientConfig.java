package com.moodleV2.Academia.jwt_security;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import proto.IdmGrpc;

import javax.annotation.PreDestroy;

@Configuration
public class GrpcClientConfig {

    private ManagedChannel channel;

    @Bean
    public ManagedChannel managedChannel() {
        channel = ManagedChannelBuilder.forAddress("localhost", 50051).usePlaintext().build();
        return channel;
    }

    @Bean
    public IdmGrpc.IdmBlockingStub idmBlockingStub(ManagedChannel channel) {
        return IdmGrpc.newBlockingStub(channel);
    }

    @PreDestroy
    public void shutdownChannel() {
        if (channel != null) {
            channel.shutdown();
        }
    }
}
