package service;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import proto.CalculatorGrpc;
import proto.CalculatorOuterClass;

public class Client {

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
        CalculatorGrpc.CalculatorBlockingStub bookStub = CalculatorGrpc.newBlockingStub(channel);

        /* *
         * An asynchronous instance of the above declaration.
         * CalculatorGrpc.CalculatorStub bookStub = CalculatorGrpc.newStub(channel);
         * */

        /* *
         * We can now use the gRPC function via our instance of GreeterBlockingStub bookStub.
         * Below we call the function sayHello(Helloworld.HelloRequest request) with name field value set to "gRPC".
         * This function returns a value of type  Helloworld.HelloReply that is saved in our instance reply.
         * We can get via generated functions every field from our message, in this example, we have just one field.
         * */

        CalculatorOuterClass.Number reply = bookStub.squareRoot(CalculatorOuterClass.Number.newBuilder().setValue(2).build());

        System.out.println(reply.getValue());

        reply = bookStub.sum(CalculatorOuterClass.SumNumbers.newBuilder().setA(16).setB(14).build());

        System.out.println(reply.getValue());

        reply = bookStub.absoluteValue(CalculatorOuterClass.Number.newBuilder().setValue((float)-29.33).build());

        System.out.println(reply.getValue());

        channel.shutdown();
    }
}
