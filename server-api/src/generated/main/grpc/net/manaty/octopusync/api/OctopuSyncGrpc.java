package net.manaty.octopusync.api;

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
    comments = "Source: server_api.proto")
public final class OctopuSyncGrpc {

  private OctopuSyncGrpc() {}

  private static <T> io.grpc.stub.StreamObserver<T> toObserver(final io.vertx.core.Handler<io.vertx.core.AsyncResult<T>> handler) {
    return new io.grpc.stub.StreamObserver<T>() {
      private volatile boolean resolved = false;
      @Override
      public void onNext(T value) {
        if (!resolved) {
          resolved = true;
          handler.handle(io.vertx.core.Future.succeededFuture(value));
        }
      }

      @Override
      public void onError(Throwable t) {
        if (!resolved) {
          resolved = true;
          handler.handle(io.vertx.core.Future.failedFuture(t));
        }
      }

      @Override
      public void onCompleted() {
        if (!resolved) {
          resolved = true;
          handler.handle(io.vertx.core.Future.succeededFuture());
        }
      }
    };
  }

  public static final String SERVICE_NAME = "net.manaty.octopusync.api.OctopuSync";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<net.manaty.octopusync.api.SendClickRequest,
      net.manaty.octopusync.api.SendClickResponse> getSendClickMethod;

  public static io.grpc.MethodDescriptor<net.manaty.octopusync.api.SendClickRequest,
      net.manaty.octopusync.api.SendClickResponse> getSendClickMethod() {
    io.grpc.MethodDescriptor<net.manaty.octopusync.api.SendClickRequest, net.manaty.octopusync.api.SendClickResponse> getSendClickMethod;
    if ((getSendClickMethod = OctopuSyncGrpc.getSendClickMethod) == null) {
      synchronized (OctopuSyncGrpc.class) {
        if ((getSendClickMethod = OctopuSyncGrpc.getSendClickMethod) == null) {
          OctopuSyncGrpc.getSendClickMethod = getSendClickMethod = 
              io.grpc.MethodDescriptor.<net.manaty.octopusync.api.SendClickRequest, net.manaty.octopusync.api.SendClickResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(
                  "net.manaty.octopusync.api.OctopuSync", "SendClick"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  net.manaty.octopusync.api.SendClickRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  net.manaty.octopusync.api.SendClickResponse.getDefaultInstance()))
                  .setSchemaDescriptor(new OctopuSyncMethodDescriptorSupplier("SendClick"))
                  .build();
          }
        }
     }
     return getSendClickMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static OctopuSyncStub newStub(io.grpc.Channel channel) {
    return new OctopuSyncStub(channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static OctopuSyncBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    return new OctopuSyncBlockingStub(channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static OctopuSyncFutureStub newFutureStub(
      io.grpc.Channel channel) {
    return new OctopuSyncFutureStub(channel);
  }

  /**
   * Creates a new vertx stub that supports all call types for the service
   */
  public static OctopuSyncVertxStub newVertxStub(io.grpc.Channel channel) {
    return new OctopuSyncVertxStub(channel);
  }

  /**
   */
  public static abstract class OctopuSyncImplBase implements io.grpc.BindableService {

    /**
     */
    public void sendClick(net.manaty.octopusync.api.SendClickRequest request,
        io.grpc.stub.StreamObserver<net.manaty.octopusync.api.SendClickResponse> responseObserver) {
      asyncUnimplementedUnaryCall(getSendClickMethod(), responseObserver);
    }

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
          .addMethod(
            getSendClickMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                net.manaty.octopusync.api.SendClickRequest,
                net.manaty.octopusync.api.SendClickResponse>(
                  this, METHODID_SEND_CLICK)))
          .build();
    }
  }

  /**
   */
  public static final class OctopuSyncStub extends io.grpc.stub.AbstractStub<OctopuSyncStub> {
    public OctopuSyncStub(io.grpc.Channel channel) {
      super(channel);
    }

    public OctopuSyncStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected OctopuSyncStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new OctopuSyncStub(channel, callOptions);
    }

    /**
     */
    public void sendClick(net.manaty.octopusync.api.SendClickRequest request,
        io.grpc.stub.StreamObserver<net.manaty.octopusync.api.SendClickResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getSendClickMethod(), getCallOptions()), request, responseObserver);
    }
  }

  /**
   */
  public static final class OctopuSyncBlockingStub extends io.grpc.stub.AbstractStub<OctopuSyncBlockingStub> {
    public OctopuSyncBlockingStub(io.grpc.Channel channel) {
      super(channel);
    }

    public OctopuSyncBlockingStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected OctopuSyncBlockingStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new OctopuSyncBlockingStub(channel, callOptions);
    }

    /**
     */
    public net.manaty.octopusync.api.SendClickResponse sendClick(net.manaty.octopusync.api.SendClickRequest request) {
      return blockingUnaryCall(
          getChannel(), getSendClickMethod(), getCallOptions(), request);
    }
  }

  /**
   */
  public static final class OctopuSyncFutureStub extends io.grpc.stub.AbstractStub<OctopuSyncFutureStub> {
    public OctopuSyncFutureStub(io.grpc.Channel channel) {
      super(channel);
    }

    public OctopuSyncFutureStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected OctopuSyncFutureStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new OctopuSyncFutureStub(channel, callOptions);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<net.manaty.octopusync.api.SendClickResponse> sendClick(
        net.manaty.octopusync.api.SendClickRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getSendClickMethod(), getCallOptions()), request);
    }
  }

  /**
   */
  public static abstract class OctopuSyncVertxImplBase implements io.grpc.BindableService {

    /**
     */
    public void sendClick(net.manaty.octopusync.api.SendClickRequest request,
        io.vertx.core.Future<net.manaty.octopusync.api.SendClickResponse> response) {
      asyncUnimplementedUnaryCall(getSendClickMethod(), OctopuSyncGrpc.toObserver(response.completer()));
    }

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
          .addMethod(
            getSendClickMethod(),
            asyncUnaryCall(
              new VertxMethodHandlers<
                net.manaty.octopusync.api.SendClickRequest,
                net.manaty.octopusync.api.SendClickResponse>(
                  this, METHODID_SEND_CLICK)))
          .build();
    }
  }

  /**
   */
  public static final class OctopuSyncVertxStub extends io.grpc.stub.AbstractStub<OctopuSyncVertxStub> {
    public OctopuSyncVertxStub(io.grpc.Channel channel) {
      super(channel);
    }

    public OctopuSyncVertxStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected OctopuSyncVertxStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new OctopuSyncVertxStub(channel, callOptions);
    }

    /**
     */
    public void sendClick(net.manaty.octopusync.api.SendClickRequest request,
        io.vertx.core.Handler<io.vertx.core.AsyncResult<net.manaty.octopusync.api.SendClickResponse>> response) {
      asyncUnaryCall(
          getChannel().newCall(getSendClickMethod(), getCallOptions()), request, OctopuSyncGrpc.toObserver(response));
    }
  }

  private static final int METHODID_SEND_CLICK = 0;

  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final OctopuSyncImplBase serviceImpl;
    private final int methodId;

    MethodHandlers(OctopuSyncImplBase serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_SEND_CLICK:
          serviceImpl.sendClick((net.manaty.octopusync.api.SendClickRequest) request,
              (io.grpc.stub.StreamObserver<net.manaty.octopusync.api.SendClickResponse>) responseObserver);
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

  private static final class VertxMethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final OctopuSyncVertxImplBase serviceImpl;
    private final int methodId;

    VertxMethodHandlers(OctopuSyncVertxImplBase serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_SEND_CLICK:
          serviceImpl.sendClick((net.manaty.octopusync.api.SendClickRequest) request,
              (io.vertx.core.Future<net.manaty.octopusync.api.SendClickResponse>) io.vertx.core.Future.<net.manaty.octopusync.api.SendClickResponse>future().setHandler(ar -> {
                if (ar.succeeded()) {
                  ((io.grpc.stub.StreamObserver<net.manaty.octopusync.api.SendClickResponse>) responseObserver).onNext(ar.result());
                  responseObserver.onCompleted();
                } else {
                  responseObserver.onError(ar.cause());
                }
              }));
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

  private static abstract class OctopuSyncBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    OctopuSyncBaseDescriptorSupplier() {}

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return net.manaty.octopusync.api.ServerApi.getDescriptor();
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("OctopuSync");
    }
  }

  private static final class OctopuSyncFileDescriptorSupplier
      extends OctopuSyncBaseDescriptorSupplier {
    OctopuSyncFileDescriptorSupplier() {}
  }

  private static final class OctopuSyncMethodDescriptorSupplier
      extends OctopuSyncBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final String methodName;

    OctopuSyncMethodDescriptorSupplier(String methodName) {
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
      synchronized (OctopuSyncGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new OctopuSyncFileDescriptorSupplier())
              .addMethod(getSendClickMethod())
              .build();
        }
      }
    }
    return result;
  }
}
