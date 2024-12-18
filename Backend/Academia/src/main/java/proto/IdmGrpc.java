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
    comments = "Source: Idm.proto")
public final class IdmGrpc {

  private IdmGrpc() {}

  public static final String SERVICE_NAME = "Idm";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<proto.IdmOuterClass.AuthRequest,
      proto.IdmOuterClass.AuthResponse> getAuthenticateMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "Authenticate",
      requestType = proto.IdmOuterClass.AuthRequest.class,
      responseType = proto.IdmOuterClass.AuthResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<proto.IdmOuterClass.AuthRequest,
      proto.IdmOuterClass.AuthResponse> getAuthenticateMethod() {
    io.grpc.MethodDescriptor<proto.IdmOuterClass.AuthRequest, proto.IdmOuterClass.AuthResponse> getAuthenticateMethod;
    if ((getAuthenticateMethod = IdmGrpc.getAuthenticateMethod) == null) {
      synchronized (IdmGrpc.class) {
        if ((getAuthenticateMethod = IdmGrpc.getAuthenticateMethod) == null) {
          IdmGrpc.getAuthenticateMethod = getAuthenticateMethod = 
              io.grpc.MethodDescriptor.<proto.IdmOuterClass.AuthRequest, proto.IdmOuterClass.AuthResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(
                  "Idm", "Authenticate"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  proto.IdmOuterClass.AuthRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  proto.IdmOuterClass.AuthResponse.getDefaultInstance()))
                  .setSchemaDescriptor(new IdmMethodDescriptorSupplier("Authenticate"))
                  .build();
          }
        }
     }
     return getAuthenticateMethod;
  }

  private static volatile io.grpc.MethodDescriptor<proto.IdmOuterClass.ValidateOrDestroyRequest,
      proto.IdmOuterClass.ValidateResponse> getValidateMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "Validate",
      requestType = proto.IdmOuterClass.ValidateOrDestroyRequest.class,
      responseType = proto.IdmOuterClass.ValidateResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<proto.IdmOuterClass.ValidateOrDestroyRequest,
      proto.IdmOuterClass.ValidateResponse> getValidateMethod() {
    io.grpc.MethodDescriptor<proto.IdmOuterClass.ValidateOrDestroyRequest, proto.IdmOuterClass.ValidateResponse> getValidateMethod;
    if ((getValidateMethod = IdmGrpc.getValidateMethod) == null) {
      synchronized (IdmGrpc.class) {
        if ((getValidateMethod = IdmGrpc.getValidateMethod) == null) {
          IdmGrpc.getValidateMethod = getValidateMethod = 
              io.grpc.MethodDescriptor.<proto.IdmOuterClass.ValidateOrDestroyRequest, proto.IdmOuterClass.ValidateResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(
                  "Idm", "Validate"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  proto.IdmOuterClass.ValidateOrDestroyRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  proto.IdmOuterClass.ValidateResponse.getDefaultInstance()))
                  .setSchemaDescriptor(new IdmMethodDescriptorSupplier("Validate"))
                  .build();
          }
        }
     }
     return getValidateMethod;
  }

  private static volatile io.grpc.MethodDescriptor<proto.IdmOuterClass.ValidateOrDestroyRequest,
      proto.IdmOuterClass.DestroyResponse> getDestroyMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "Destroy",
      requestType = proto.IdmOuterClass.ValidateOrDestroyRequest.class,
      responseType = proto.IdmOuterClass.DestroyResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<proto.IdmOuterClass.ValidateOrDestroyRequest,
      proto.IdmOuterClass.DestroyResponse> getDestroyMethod() {
    io.grpc.MethodDescriptor<proto.IdmOuterClass.ValidateOrDestroyRequest, proto.IdmOuterClass.DestroyResponse> getDestroyMethod;
    if ((getDestroyMethod = IdmGrpc.getDestroyMethod) == null) {
      synchronized (IdmGrpc.class) {
        if ((getDestroyMethod = IdmGrpc.getDestroyMethod) == null) {
          IdmGrpc.getDestroyMethod = getDestroyMethod = 
              io.grpc.MethodDescriptor.<proto.IdmOuterClass.ValidateOrDestroyRequest, proto.IdmOuterClass.DestroyResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(
                  "Idm", "Destroy"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  proto.IdmOuterClass.ValidateOrDestroyRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  proto.IdmOuterClass.DestroyResponse.getDefaultInstance()))
                  .setSchemaDescriptor(new IdmMethodDescriptorSupplier("Destroy"))
                  .build();
          }
        }
     }
     return getDestroyMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static IdmStub newStub(io.grpc.Channel channel) {
    return new IdmStub(channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static IdmBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    return new IdmBlockingStub(channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static IdmFutureStub newFutureStub(
      io.grpc.Channel channel) {
    return new IdmFutureStub(channel);
  }

  /**
   */
  public static abstract class IdmImplBase implements io.grpc.BindableService {

    /**
     */
    public void authenticate(proto.IdmOuterClass.AuthRequest request,
        io.grpc.stub.StreamObserver<proto.IdmOuterClass.AuthResponse> responseObserver) {
      asyncUnimplementedUnaryCall(getAuthenticateMethod(), responseObserver);
    }

    /**
     */
    public void validate(proto.IdmOuterClass.ValidateOrDestroyRequest request,
        io.grpc.stub.StreamObserver<proto.IdmOuterClass.ValidateResponse> responseObserver) {
      asyncUnimplementedUnaryCall(getValidateMethod(), responseObserver);
    }

    /**
     */
    public void destroy(proto.IdmOuterClass.ValidateOrDestroyRequest request,
        io.grpc.stub.StreamObserver<proto.IdmOuterClass.DestroyResponse> responseObserver) {
      asyncUnimplementedUnaryCall(getDestroyMethod(), responseObserver);
    }

    @Override public final io.grpc.ServerServiceDefinition bindService() {
      return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
          .addMethod(
            getAuthenticateMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                proto.IdmOuterClass.AuthRequest,
                proto.IdmOuterClass.AuthResponse>(
                  this, METHODID_AUTHENTICATE)))
          .addMethod(
            getValidateMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                proto.IdmOuterClass.ValidateOrDestroyRequest,
                proto.IdmOuterClass.ValidateResponse>(
                  this, METHODID_VALIDATE)))
          .addMethod(
            getDestroyMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                proto.IdmOuterClass.ValidateOrDestroyRequest,
                proto.IdmOuterClass.DestroyResponse>(
                  this, METHODID_DESTROY)))
          .build();
    }
  }

  /**
   */
  public static final class IdmStub extends io.grpc.stub.AbstractStub<IdmStub> {
    private IdmStub(io.grpc.Channel channel) {
      super(channel);
    }

    private IdmStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @Override
    protected IdmStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new IdmStub(channel, callOptions);
    }

    /**
     */
    public void authenticate(proto.IdmOuterClass.AuthRequest request,
        io.grpc.stub.StreamObserver<proto.IdmOuterClass.AuthResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getAuthenticateMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void validate(proto.IdmOuterClass.ValidateOrDestroyRequest request,
        io.grpc.stub.StreamObserver<proto.IdmOuterClass.ValidateResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getValidateMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void destroy(proto.IdmOuterClass.ValidateOrDestroyRequest request,
        io.grpc.stub.StreamObserver<proto.IdmOuterClass.DestroyResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getDestroyMethod(), getCallOptions()), request, responseObserver);
    }
  }

  /**
   */
  public static final class IdmBlockingStub extends io.grpc.stub.AbstractStub<IdmBlockingStub> {
    private IdmBlockingStub(io.grpc.Channel channel) {
      super(channel);
    }

    private IdmBlockingStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @Override
    protected IdmBlockingStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new IdmBlockingStub(channel, callOptions);
    }

    /**
     */
    public proto.IdmOuterClass.AuthResponse authenticate(proto.IdmOuterClass.AuthRequest request) {
      return blockingUnaryCall(
          getChannel(), getAuthenticateMethod(), getCallOptions(), request);
    }

    /**
     */
    public proto.IdmOuterClass.ValidateResponse validate(proto.IdmOuterClass.ValidateOrDestroyRequest request) {
      return blockingUnaryCall(
          getChannel(), getValidateMethod(), getCallOptions(), request);
    }

    /**
     */
    public proto.IdmOuterClass.DestroyResponse destroy(proto.IdmOuterClass.ValidateOrDestroyRequest request) {
      return blockingUnaryCall(
          getChannel(), getDestroyMethod(), getCallOptions(), request);
    }
  }

  /**
   */
  public static final class IdmFutureStub extends io.grpc.stub.AbstractStub<IdmFutureStub> {
    private IdmFutureStub(io.grpc.Channel channel) {
      super(channel);
    }

    private IdmFutureStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @Override
    protected IdmFutureStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new IdmFutureStub(channel, callOptions);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<proto.IdmOuterClass.AuthResponse> authenticate(
        proto.IdmOuterClass.AuthRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getAuthenticateMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<proto.IdmOuterClass.ValidateResponse> validate(
        proto.IdmOuterClass.ValidateOrDestroyRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getValidateMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<proto.IdmOuterClass.DestroyResponse> destroy(
        proto.IdmOuterClass.ValidateOrDestroyRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getDestroyMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_AUTHENTICATE = 0;
  private static final int METHODID_VALIDATE = 1;
  private static final int METHODID_DESTROY = 2;

  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final IdmImplBase serviceImpl;
    private final int methodId;

    MethodHandlers(IdmImplBase serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_AUTHENTICATE:
          serviceImpl.authenticate((proto.IdmOuterClass.AuthRequest) request,
              (io.grpc.stub.StreamObserver<proto.IdmOuterClass.AuthResponse>) responseObserver);
          break;
        case METHODID_VALIDATE:
          serviceImpl.validate((proto.IdmOuterClass.ValidateOrDestroyRequest) request,
              (io.grpc.stub.StreamObserver<proto.IdmOuterClass.ValidateResponse>) responseObserver);
          break;
        case METHODID_DESTROY:
          serviceImpl.destroy((proto.IdmOuterClass.ValidateOrDestroyRequest) request,
              (io.grpc.stub.StreamObserver<proto.IdmOuterClass.DestroyResponse>) responseObserver);
          break;
        default:
          throw new AssertionError();
      }
    }

    @Override
    @SuppressWarnings("unchecked")
    public io.grpc.stub.StreamObserver<Req> invoke(
        io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        default:
          throw new AssertionError();
      }
    }
  }

  private static abstract class IdmBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    IdmBaseDescriptorSupplier() {}

    @Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return proto.IdmOuterClass.getDescriptor();
    }

    @Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("Idm");
    }
  }

  private static final class IdmFileDescriptorSupplier
      extends IdmBaseDescriptorSupplier {
    IdmFileDescriptorSupplier() {}
  }

  private static final class IdmMethodDescriptorSupplier
      extends IdmBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final String methodName;

    IdmMethodDescriptorSupplier(String methodName) {
      this.methodName = methodName;
    }

    @Override
    public com.google.protobuf.Descriptors.MethodDescriptor getMethodDescriptor() {
      return getServiceDescriptor().findMethodByName(methodName);
    }
  }

  private static volatile io.grpc.ServiceDescriptor serviceDescriptor;

  public static io.grpc.ServiceDescriptor getServiceDescriptor() {
    io.grpc.ServiceDescriptor result = serviceDescriptor;
    if (result == null) {
      synchronized (IdmGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new IdmFileDescriptorSupplier())
              .addMethod(getAuthenticateMethod())
              .addMethod(getValidateMethod())
              .addMethod(getDestroyMethod())
              .build();
        }
      }
    }
    return result;
  }
}
