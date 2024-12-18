package proto;

import static io.grpc.MethodDescriptor.generateFullMethodName;
import static io.grpc.stub.ClientCalls.asyncBidiStreamingCall;
import static io.grpc.stub.ClientCalls.asyncClientStreamingCall;
import static io.grpc.stub.ClientCalls.asyncServerStreamingCall;
import static io.grpc.stub.ClientCalls.asyncUnaryCall;
import static io.grpc.stub.ClientCalls.blockingServerStreamingCall;
import static io.grpc.stub.ClientCalls.blockingUnaryCall;
import static io.grpc.stub.ClientCalls.futureUnaryCall;
import static io.grpc.stub.ServerCalls.asyncBidiStreamingCall;
import static io.grpc.stub.ServerCalls.asyncClientStreamingCall;
import static io.grpc.stub.ServerCalls.asyncServerStreamingCall;
import static io.grpc.stub.ServerCalls.asyncUnaryCall;
import static io.grpc.stub.ServerCalls.asyncUnimplementedStreamingCall;
import static io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall;

/**
 */
@javax.annotation.Generated(
    value = "by gRPC proto compiler (version 1.15.0)",
    comments = "Source: Calculator.proto")
public final class CalculatorGrpc {

  private CalculatorGrpc() {}

  public static final String SERVICE_NAME = "Calculator";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<proto.CalculatorOuterClass.Number,
      proto.CalculatorOuterClass.Number> getSquareRootMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "SquareRoot",
      requestType = proto.CalculatorOuterClass.Number.class,
      responseType = proto.CalculatorOuterClass.Number.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<proto.CalculatorOuterClass.Number,
      proto.CalculatorOuterClass.Number> getSquareRootMethod() {
    io.grpc.MethodDescriptor<proto.CalculatorOuterClass.Number, proto.CalculatorOuterClass.Number> getSquareRootMethod;
    if ((getSquareRootMethod = CalculatorGrpc.getSquareRootMethod) == null) {
      synchronized (CalculatorGrpc.class) {
        if ((getSquareRootMethod = CalculatorGrpc.getSquareRootMethod) == null) {
          CalculatorGrpc.getSquareRootMethod = getSquareRootMethod = 
              io.grpc.MethodDescriptor.<proto.CalculatorOuterClass.Number, proto.CalculatorOuterClass.Number>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(
                  "Calculator", "SquareRoot"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  proto.CalculatorOuterClass.Number.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  proto.CalculatorOuterClass.Number.getDefaultInstance()))
                  .setSchemaDescriptor(new CalculatorMethodDescriptorSupplier("SquareRoot"))
                  .build();
          }
        }
     }
     return getSquareRootMethod;
  }

  private static volatile io.grpc.MethodDescriptor<proto.CalculatorOuterClass.Number,
      proto.CalculatorOuterClass.Number> getAbsoluteValueMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "AbsoluteValue",
      requestType = proto.CalculatorOuterClass.Number.class,
      responseType = proto.CalculatorOuterClass.Number.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<proto.CalculatorOuterClass.Number,
      proto.CalculatorOuterClass.Number> getAbsoluteValueMethod() {
    io.grpc.MethodDescriptor<proto.CalculatorOuterClass.Number, proto.CalculatorOuterClass.Number> getAbsoluteValueMethod;
    if ((getAbsoluteValueMethod = CalculatorGrpc.getAbsoluteValueMethod) == null) {
      synchronized (CalculatorGrpc.class) {
        if ((getAbsoluteValueMethod = CalculatorGrpc.getAbsoluteValueMethod) == null) {
          CalculatorGrpc.getAbsoluteValueMethod = getAbsoluteValueMethod = 
              io.grpc.MethodDescriptor.<proto.CalculatorOuterClass.Number, proto.CalculatorOuterClass.Number>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(
                  "Calculator", "AbsoluteValue"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  proto.CalculatorOuterClass.Number.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  proto.CalculatorOuterClass.Number.getDefaultInstance()))
                  .setSchemaDescriptor(new CalculatorMethodDescriptorSupplier("AbsoluteValue"))
                  .build();
          }
        }
     }
     return getAbsoluteValueMethod;
  }

  private static volatile io.grpc.MethodDescriptor<proto.CalculatorOuterClass.SumNumbers,
      proto.CalculatorOuterClass.Number> getSumMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "Sum",
      requestType = proto.CalculatorOuterClass.SumNumbers.class,
      responseType = proto.CalculatorOuterClass.Number.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<proto.CalculatorOuterClass.SumNumbers,
      proto.CalculatorOuterClass.Number> getSumMethod() {
    io.grpc.MethodDescriptor<proto.CalculatorOuterClass.SumNumbers, proto.CalculatorOuterClass.Number> getSumMethod;
    if ((getSumMethod = CalculatorGrpc.getSumMethod) == null) {
      synchronized (CalculatorGrpc.class) {
        if ((getSumMethod = CalculatorGrpc.getSumMethod) == null) {
          CalculatorGrpc.getSumMethod = getSumMethod = 
              io.grpc.MethodDescriptor.<proto.CalculatorOuterClass.SumNumbers, proto.CalculatorOuterClass.Number>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(
                  "Calculator", "Sum"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  proto.CalculatorOuterClass.SumNumbers.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  proto.CalculatorOuterClass.Number.getDefaultInstance()))
                  .setSchemaDescriptor(new CalculatorMethodDescriptorSupplier("Sum"))
                  .build();
          }
        }
     }
     return getSumMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static CalculatorStub newStub(io.grpc.Channel channel) {
    return new CalculatorStub(channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static CalculatorBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    return new CalculatorBlockingStub(channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static CalculatorFutureStub newFutureStub(
      io.grpc.Channel channel) {
    return new CalculatorFutureStub(channel);
  }

  /**
   */
  public static abstract class CalculatorImplBase implements io.grpc.BindableService {

    /**
     */
    public void squareRoot(proto.CalculatorOuterClass.Number request,
        io.grpc.stub.StreamObserver<proto.CalculatorOuterClass.Number> responseObserver) {
      asyncUnimplementedUnaryCall(getSquareRootMethod(), responseObserver);
    }

    /**
     */
    public void absoluteValue(proto.CalculatorOuterClass.Number request,
        io.grpc.stub.StreamObserver<proto.CalculatorOuterClass.Number> responseObserver) {
      asyncUnimplementedUnaryCall(getAbsoluteValueMethod(), responseObserver);
    }

    /**
     */
    public void sum(proto.CalculatorOuterClass.SumNumbers request,
        io.grpc.stub.StreamObserver<proto.CalculatorOuterClass.Number> responseObserver) {
      asyncUnimplementedUnaryCall(getSumMethod(), responseObserver);
    }

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
          .addMethod(
            getSquareRootMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                proto.CalculatorOuterClass.Number,
                proto.CalculatorOuterClass.Number>(
                  this, METHODID_SQUARE_ROOT)))
          .addMethod(
            getAbsoluteValueMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                proto.CalculatorOuterClass.Number,
                proto.CalculatorOuterClass.Number>(
                  this, METHODID_ABSOLUTE_VALUE)))
          .addMethod(
            getSumMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                proto.CalculatorOuterClass.SumNumbers,
                proto.CalculatorOuterClass.Number>(
                  this, METHODID_SUM)))
          .build();
    }
  }

  /**
   */
  public static final class CalculatorStub extends io.grpc.stub.AbstractStub<CalculatorStub> {
    private CalculatorStub(io.grpc.Channel channel) {
      super(channel);
    }

    private CalculatorStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected CalculatorStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new CalculatorStub(channel, callOptions);
    }

    /**
     */
    public void squareRoot(proto.CalculatorOuterClass.Number request,
        io.grpc.stub.StreamObserver<proto.CalculatorOuterClass.Number> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getSquareRootMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void absoluteValue(proto.CalculatorOuterClass.Number request,
        io.grpc.stub.StreamObserver<proto.CalculatorOuterClass.Number> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getAbsoluteValueMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void sum(proto.CalculatorOuterClass.SumNumbers request,
        io.grpc.stub.StreamObserver<proto.CalculatorOuterClass.Number> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getSumMethod(), getCallOptions()), request, responseObserver);
    }
  }

  /**
   */
  public static final class CalculatorBlockingStub extends io.grpc.stub.AbstractStub<CalculatorBlockingStub> {
    private CalculatorBlockingStub(io.grpc.Channel channel) {
      super(channel);
    }

    private CalculatorBlockingStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected CalculatorBlockingStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new CalculatorBlockingStub(channel, callOptions);
    }

    /**
     */
    public proto.CalculatorOuterClass.Number squareRoot(proto.CalculatorOuterClass.Number request) {
      return blockingUnaryCall(
          getChannel(), getSquareRootMethod(), getCallOptions(), request);
    }

    /**
     */
    public proto.CalculatorOuterClass.Number absoluteValue(proto.CalculatorOuterClass.Number request) {
      return blockingUnaryCall(
          getChannel(), getAbsoluteValueMethod(), getCallOptions(), request);
    }

    /**
     */
    public proto.CalculatorOuterClass.Number sum(proto.CalculatorOuterClass.SumNumbers request) {
      return blockingUnaryCall(
          getChannel(), getSumMethod(), getCallOptions(), request);
    }
  }

  /**
   */
  public static final class CalculatorFutureStub extends io.grpc.stub.AbstractStub<CalculatorFutureStub> {
    private CalculatorFutureStub(io.grpc.Channel channel) {
      super(channel);
    }

    private CalculatorFutureStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected CalculatorFutureStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new CalculatorFutureStub(channel, callOptions);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<proto.CalculatorOuterClass.Number> squareRoot(
        proto.CalculatorOuterClass.Number request) {
      return futureUnaryCall(
          getChannel().newCall(getSquareRootMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<proto.CalculatorOuterClass.Number> absoluteValue(
        proto.CalculatorOuterClass.Number request) {
      return futureUnaryCall(
          getChannel().newCall(getAbsoluteValueMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<proto.CalculatorOuterClass.Number> sum(
        proto.CalculatorOuterClass.SumNumbers request) {
      return futureUnaryCall(
          getChannel().newCall(getSumMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_SQUARE_ROOT = 0;
  private static final int METHODID_ABSOLUTE_VALUE = 1;
  private static final int METHODID_SUM = 2;

  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final CalculatorImplBase serviceImpl;
    private final int methodId;

    MethodHandlers(CalculatorImplBase serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_SQUARE_ROOT:
          serviceImpl.squareRoot((proto.CalculatorOuterClass.Number) request,
              (io.grpc.stub.StreamObserver<proto.CalculatorOuterClass.Number>) responseObserver);
          break;
        case METHODID_ABSOLUTE_VALUE:
          serviceImpl.absoluteValue((proto.CalculatorOuterClass.Number) request,
              (io.grpc.stub.StreamObserver<proto.CalculatorOuterClass.Number>) responseObserver);
          break;
        case METHODID_SUM:
          serviceImpl.sum((proto.CalculatorOuterClass.SumNumbers) request,
              (io.grpc.stub.StreamObserver<proto.CalculatorOuterClass.Number>) responseObserver);
          break;
        default:
          throw new AssertionError();
      }
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public io.grpc.stub.StreamObserver<Req> invoke(
        io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        default:
          throw new AssertionError();
      }
    }
  }

  private static abstract class CalculatorBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    CalculatorBaseDescriptorSupplier() {}

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return proto.CalculatorOuterClass.getDescriptor();
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("Calculator");
    }
  }

  private static final class CalculatorFileDescriptorSupplier
      extends CalculatorBaseDescriptorSupplier {
    CalculatorFileDescriptorSupplier() {}
  }

  private static final class CalculatorMethodDescriptorSupplier
      extends CalculatorBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final String methodName;

    CalculatorMethodDescriptorSupplier(String methodName) {
      this.methodName = methodName;
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.MethodDescriptor getMethodDescriptor() {
      return getServiceDescriptor().findMethodByName(methodName);
    }
  }

  private static volatile io.grpc.ServiceDescriptor serviceDescriptor;

  public static io.grpc.ServiceDescriptor getServiceDescriptor() {
    io.grpc.ServiceDescriptor result = serviceDescriptor;
    if (result == null) {
      synchronized (CalculatorGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new CalculatorFileDescriptorSupplier())
              .addMethod(getSquareRootMethod())
              .addMethod(getAbsoluteValueMethod())
              .addMethod(getSumMethod())
              .build();
        }
      }
    }
    return result;
  }
}
