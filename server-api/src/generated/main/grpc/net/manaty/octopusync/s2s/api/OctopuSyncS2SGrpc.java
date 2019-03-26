package net.manaty.octopusync.s2s.api;

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
    comments = "Source: s2s_api.proto")
public final class OctopuSyncS2SGrpc {

  private OctopuSyncS2SGrpc() {}

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

  public static final String SERVICE_NAME = "net.manaty.octopusync.s2s.api.OctopuSyncS2S";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<net.manaty.octopusync.s2s.api.SyncTimeRequest,
      net.manaty.octopusync.s2s.api.SyncTimeResponse> getSyncTimeMethod;

  public static io.grpc.MethodDescriptor<net.manaty.octopusync.s2s.api.SyncTimeRequest,
      net.manaty.octopusync.s2s.api.SyncTimeResponse> getSyncTimeMethod() {
    io.grpc.MethodDescriptor<net.manaty.octopusync.s2s.api.SyncTimeRequest, net.manaty.octopusync.s2s.api.SyncTimeResponse> getSyncTimeMethod;
    if ((getSyncTimeMethod = OctopuSyncS2SGrpc.getSyncTimeMethod) == null) {
      synchronized (OctopuSyncS2SGrpc.class) {
        if ((getSyncTimeMethod = OctopuSyncS2SGrpc.getSyncTimeMethod) == null) {
          OctopuSyncS2SGrpc.getSyncTimeMethod = getSyncTimeMethod = 
              io.grpc.MethodDescriptor.<net.manaty.octopusync.s2s.api.SyncTimeRequest, net.manaty.octopusync.s2s.api.SyncTimeResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.BIDI_STREAMING)
              .setFullMethodName(generateFullMethodName(
                  "net.manaty.octopusync.s2s.api.OctopuSyncS2S", "SyncTime"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  net.manaty.octopusync.s2s.api.SyncTimeRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  net.manaty.octopusync.s2s.api.SyncTimeResponse.getDefaultInstance()))
                  .setSchemaDescriptor(new OctopuSyncS2SMethodDescriptorSupplier("SyncTime"))
                  .build();
          }
        }
     }
     return getSyncTimeMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static OctopuSyncS2SStub newStub(io.grpc.Channel channel) {
    return new OctopuSyncS2SStub(channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static OctopuSyncS2SBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    return new OctopuSyncS2SBlockingStub(channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static OctopuSyncS2SFutureStub newFutureStub(
      io.grpc.Channel channel) {
    return new OctopuSyncS2SFutureStub(channel);
  }

  /**
   * Creates a new vertx stub that supports all call types for the service
   */
  public static OctopuSyncS2SVertxStub newVertxStub(io.grpc.Channel channel) {
    return new OctopuSyncS2SVertxStub(channel);
  }

  /**
   */
  public static abstract class OctopuSyncS2SImplBase implements io.grpc.BindableService {

    /**
     */
    public io.grpc.stub.StreamObserver<net.manaty.octopusync.s2s.api.SyncTimeRequest> syncTime(
        io.grpc.stub.StreamObserver<net.manaty.octopusync.s2s.api.SyncTimeResponse> responseObserver) {
      return asyncUnimplementedStreamingCall(getSyncTimeMethod(), responseObserver);
    }

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
          .addMethod(
            getSyncTimeMethod(),
            asyncBidiStreamingCall(
              new MethodHandlers<
                net.manaty.octopusync.s2s.api.SyncTimeRequest,
                net.manaty.octopusync.s2s.api.SyncTimeResponse>(
                  this, METHODID_SYNC_TIME)))
          .build();
    }
  }

  /**
   */
  public static final class OctopuSyncS2SStub extends io.grpc.stub.AbstractStub<OctopuSyncS2SStub> {
    public OctopuSyncS2SStub(io.grpc.Channel channel) {
      super(channel);
    }

    public OctopuSyncS2SStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected OctopuSyncS2SStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new OctopuSyncS2SStub(channel, callOptions);
    }

    /**
     */
    public io.grpc.stub.StreamObserver<net.manaty.octopusync.s2s.api.SyncTimeRequest> syncTime(
        io.grpc.stub.StreamObserver<net.manaty.octopusync.s2s.api.SyncTimeResponse> responseObserver) {
      return asyncBidiStreamingCall(
          getChannel().newCall(getSyncTimeMethod(), getCallOptions()), responseObserver);
    }
  }

  /**
   */
  public static final class OctopuSyncS2SBlockingStub extends io.grpc.stub.AbstractStub<OctopuSyncS2SBlockingStub> {
    public OctopuSyncS2SBlockingStub(io.grpc.Channel channel) {
      super(channel);
    }

    public OctopuSyncS2SBlockingStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected OctopuSyncS2SBlockingStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new OctopuSyncS2SBlockingStub(channel, callOptions);
    }
  }

  /**
   */
  public static final class OctopuSyncS2SFutureStub extends io.grpc.stub.AbstractStub<OctopuSyncS2SFutureStub> {
    public OctopuSyncS2SFutureStub(io.grpc.Channel channel) {
      super(channel);
    }

    public OctopuSyncS2SFutureStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected OctopuSyncS2SFutureStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new OctopuSyncS2SFutureStub(channel, callOptions);
    }
  }

  /**
   */
  public static abstract class OctopuSyncS2SVertxImplBase implements io.grpc.BindableService {

    /**
     */
    public void syncTime(
        io.vertx.grpc.GrpcBidiExchange<net.manaty.octopusync.s2s.api.SyncTimeRequest, net.manaty.octopusync.s2s.api.SyncTimeResponse> exchange) {
      exchange.setReadObserver(asyncUnimplementedStreamingCall(getSyncTimeMethod(), exchange.writeObserver()));
    }

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
          .addMethod(
            getSyncTimeMethod(),
            asyncBidiStreamingCall(
              new VertxMethodHandlers<
                net.manaty.octopusync.s2s.api.SyncTimeRequest,
                net.manaty.octopusync.s2s.api.SyncTimeResponse>(
                  this, METHODID_SYNC_TIME)))
          .build();
    }
  }

  /**
   */
  public static final class OctopuSyncS2SVertxStub extends io.grpc.stub.AbstractStub<OctopuSyncS2SVertxStub> {
    public OctopuSyncS2SVertxStub(io.grpc.Channel channel) {
      super(channel);
    }

    public OctopuSyncS2SVertxStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected OctopuSyncS2SVertxStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new OctopuSyncS2SVertxStub(channel, callOptions);
    }

    /**
     */
    public void syncTime(io.vertx.core.Handler<
        io.vertx.grpc.GrpcBidiExchange<net.manaty.octopusync.s2s.api.SyncTimeResponse, net.manaty.octopusync.s2s.api.SyncTimeRequest>> handler) {
      final io.vertx.grpc.GrpcReadStream<net.manaty.octopusync.s2s.api.SyncTimeResponse> readStream =
          io.vertx.grpc.GrpcReadStream.<net.manaty.octopusync.s2s.api.SyncTimeResponse>create();

      handler.handle(io.vertx.grpc.GrpcBidiExchange.create(readStream, asyncBidiStreamingCall(
          getChannel().newCall(getSyncTimeMethod(), getCallOptions()), readStream.readObserver())));
    }
  }

  private static final int METHODID_SYNC_TIME = 0;

  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final OctopuSyncS2SImplBase serviceImpl;
    private final int methodId;

    MethodHandlers(OctopuSyncS2SImplBase serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        default:
          throw new AssertionError();
      }
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public io.grpc.stub.StreamObserver<Req> invoke(
        io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_SYNC_TIME:
          return (io.grpc.stub.StreamObserver<Req>) serviceImpl.syncTime(
              (io.grpc.stub.StreamObserver<net.manaty.octopusync.s2s.api.SyncTimeResponse>) responseObserver);
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
    private final OctopuSyncS2SVertxImplBase serviceImpl;
    private final int methodId;

    VertxMethodHandlers(OctopuSyncS2SVertxImplBase serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        default:
          throw new AssertionError();
      }
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public io.grpc.stub.StreamObserver<Req> invoke(
        io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_SYNC_TIME:
          io.vertx.grpc.GrpcReadStream<net.manaty.octopusync.s2s.api.SyncTimeRequest> request0 = io.vertx.grpc.GrpcReadStream.<net.manaty.octopusync.s2s.api.SyncTimeRequest>create();
          serviceImpl.syncTime(
             io.vertx.grpc.GrpcBidiExchange.<net.manaty.octopusync.s2s.api.SyncTimeRequest, net.manaty.octopusync.s2s.api.SyncTimeResponse>create(
               request0,
               (io.grpc.stub.StreamObserver<net.manaty.octopusync.s2s.api.SyncTimeResponse>) responseObserver));
          return (io.grpc.stub.StreamObserver<Req>) request0.readObserver();
        default:
          throw new AssertionError();
      }
    }
  }

  private static abstract class OctopuSyncS2SBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    OctopuSyncS2SBaseDescriptorSupplier() {}

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return net.manaty.octopusync.s2s.api.S2SApi.getDescriptor();
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("OctopuSyncS2S");
    }
  }

  private static final class OctopuSyncS2SFileDescriptorSupplier
      extends OctopuSyncS2SBaseDescriptorSupplier {
    OctopuSyncS2SFileDescriptorSupplier() {}
  }

  private static final class OctopuSyncS2SMethodDescriptorSupplier
      extends OctopuSyncS2SBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final String methodName;

    OctopuSyncS2SMethodDescriptorSupplier(String methodName) {
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
      synchronized (OctopuSyncS2SGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new OctopuSyncS2SFileDescriptorSupplier())
              .addMethod(getSyncTimeMethod())
              .build();
        }
      }
    }
    return result;
  }
}
