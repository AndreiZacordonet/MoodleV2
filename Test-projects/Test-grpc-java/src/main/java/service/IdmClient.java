package service;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import proto.CalculatorGrpc;
import proto.CalculatorOuterClass;
import proto.IdmGrpc;
import proto.IdmOuterClass;

public class IdmClient {
    public static void main(String[] args) {
        /* *
         * Establish a connection to the server using the class ManagedChannelBuilder and the function usePlaintext().
         * The function usePlainText() should only be used for testing or for APIs where the use of such API or the data
         * exchanged is not sensitive.
         * */
        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 50051).usePlaintext().build();

        /* *
         * A blocking-style stub instance of Greeter service. We can have two types of stubs: blocking and async.
         * Blocking stubs are synchronous. Non-blocking stubs are asynchronous.
         * Take care if you want to call an RPC function on a blocking stub from UI thread
         * (cause an unresponsive/laggy UI).
         * */
        IdmGrpc.IdmBlockingStub stub = IdmGrpc.newBlockingStub(channel);

        /* *
         * An asynchronous instance of the above declaration.
         * IdmGrpc.IdmStub stub = IdmGrpc.newStub(channel);
         * */

        /* *
         * We can now use the gRPC function via our instance of GreeterBlockingStub bookStub.
         * Below we call the function sayHello(Helloworld.HelloRequest request) with name field value set to "gRPC".
         * This function returns a value of type  Helloworld.HelloReply that is saved in our instance reply.
         * We can get via generated functions every field from our message, in this example, we have just one field.
         * */

        IdmOuterClass.AuthResponse authResponse = stub.authenticate(IdmOuterClass.AuthRequest.newBuilder().setEmail("andrei74c0@gmail.com").setPassword("pass1234").build());

        System.out.println(authResponse.getToken());

        // TODO: catch io.grpc.StatusRuntimeException
        IdmOuterClass.ValidateResponse validateResponse = stub.validate(IdmOuterClass.ValidateOrDestroyRequest.newBuilder().setToken(authResponse.getToken()).build());

        System.out.println("Sub: " + validateResponse.getSub() + "\tRole: " + validateResponse.getRole());

//        validateResponse = stub.validate(IdmOuterClass.ValidateOrDestroyRequest.newBuilder().setToken(authResponse.getToken() + "1").build());
//
//        System.out.println("Sub: " + validateResponse.getSub() + "\tRole: " + validateResponse.getRole());

        // TODO: catch io.grpc.StatusRuntimeException
        IdmOuterClass.DestroyResponse destroyResponse = stub.destroy(IdmOuterClass.ValidateOrDestroyRequest.newBuilder().setToken("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJodHRwOi8vbG9jYWxob3N0OjgwODAvYXBpL2FjYWRlbWlhIiwic3ViIjoiMSIsImV4cCI6MTczNDQ0Mjc0NiwianRpIjoiYjAxM2RhMzktOTJlYi00MDJmLTg3Y2YtNTljNGIwMTNkODk0Iiwicm9sZSI6IkFETUlOIn0.T3nERPnNnrcQA53aXOqr--vGZr1mHLVeTREO5x71pjU").build());

        System.out.println(destroyResponse.getSuccess());

        channel.shutdown();
    }
}
