package service;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import proto.IdmGrpc;
import proto.IdmOuterClass;

import java.util.Scanner;

public class IdmClient {
    public static void main(String[] args) {
        // Create a connection to the gRPC server
        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 50051).usePlaintext().build();
        IdmGrpc.IdmBlockingStub stub = IdmGrpc.newBlockingStub(channel);

        Scanner scanner = new Scanner(System.in);
        System.out.println("Connected to gRPC server on localhost:50051.");

        while (true) {
            System.out.println("\nEnter command: login / validate / destroy / exit :");
            String command = scanner.nextLine().trim().toLowerCase();

            if ("exit".equals(command)) {
                System.out.println("Exiting client...");
                break;
            }

            try {
                switch (command) {
                    case "login" -> {
                        System.out.println("\nEnter credentials for authentication");
                        System.out.print("Email: ");
                        String email = scanner.nextLine().trim();

                        System.out.print("Password: ");
                        String password = scanner.nextLine().trim();

                        IdmOuterClass.AuthRequest authRequest = IdmOuterClass.AuthRequest.newBuilder()
                                .setEmail(email)
                                .setPassword(password)
                                .build();
                        IdmOuterClass.AuthResponse authResponse = stub.authenticate(authRequest);
                        System.out.println("Received token: " + authResponse.getToken());
                    }

                    case "validate" -> {
                        System.out.println("\nEnter JWT:");
                        String jwt = scanner.nextLine().trim();

                        IdmOuterClass.ValidateOrDestroyRequest validateRequest = IdmOuterClass.ValidateOrDestroyRequest.newBuilder()
                                .setToken(jwt)
                                .build();
                        IdmOuterClass.ValidateResponse validateResponse = stub.validate(validateRequest);
                        System.out.println("Response:\nSub: " + validateResponse.getSub() + "\tRole: " + validateResponse.getRole());
                    }

                    case "destroy" -> {
                        System.out.println("\nEnter JWT:");
                        String jwt = scanner.nextLine().trim();

                        IdmOuterClass.ValidateOrDestroyRequest destroyRequest = IdmOuterClass.ValidateOrDestroyRequest.newBuilder()
                                .setToken(jwt)
                                .build();
                        IdmOuterClass.DestroyResponse destroyResponse = stub.destroy(destroyRequest);
                        System.out.println("Response: " + destroyResponse.getSuccess());
                    }

                    default -> System.out.println("Invalid command. Please try again.");
                }
            } catch (io.grpc.StatusRuntimeException e) {
                System.err.println("Error: " + e.getStatus().getCode() + " - " + e.getStatus().getDescription());
            }
        }

        // Shutdown the channel
        channel.shutdown();
    }
}
