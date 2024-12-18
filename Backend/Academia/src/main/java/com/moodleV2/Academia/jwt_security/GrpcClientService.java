package com.moodleV2.Academia.jwt_security;

import org.springframework.stereotype.Service;
import proto.IdmGrpc;
import proto.IdmOuterClass;

@Service
public class GrpcClientService {

    private final IdmGrpc.IdmBlockingStub idmBlockingStub;

    public GrpcClientService(IdmGrpc.IdmBlockingStub idmBlockingStub) {
        this.idmBlockingStub = idmBlockingStub;
    }

    public String validateTokenAndGetRole(String token) {
        try {
            IdmOuterClass.ValidateOrDestroyRequest request = IdmOuterClass.ValidateOrDestroyRequest.newBuilder()
                    .setToken(token)
                    .build();
            IdmOuterClass.ValidateResponse response = idmBlockingStub.validate(request);
            return response.getRole();
        } catch (Exception e) {
            throw new RuntimeException("Failed to validate token via gRPC: " + e.getMessage());
        }
    }
}
