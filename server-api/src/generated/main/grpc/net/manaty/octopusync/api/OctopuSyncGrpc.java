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
  private static volatile io.grpc.MethodDescriptor<net.manaty.octopusync.api.GetHeadsetsRequest,
      net.manaty.octopusync.api.GetHeadsetsResponse> getGetHeadsetsMethod;

  public static io.grpc.MethodDescriptor<net.manaty.octopusync.api.GetHeadsetsRequest,
      net.manaty.octopusync.api.GetHeadsetsResponse> getGetHeadsetsMethod() {
    io.grpc.MethodDescriptor<net.manaty.octopusync.api.GetHeadsetsRequest, net.manaty.octopusync.api.GetHeadsetsResponse> getGetHeadsetsMethod;
    if ((getGetHeadsetsMethod = OctopuSyncGrpc.getGetHeadsetsMethod) == null) {
      synchronized (OctopuSyncGrpc.class) {
        if ((getGetHeadsetsMethod = OctopuSyncGrpc.getGetHeadsetsMethod) == null) {
          OctopuSyncGrpc.getGetHeadsetsMethod = getGetHeadsetsMethod = 
              io.grpc.MethodDescriptor.<net.manaty.octopusync.api.GetHeadsetsRequest, net.manaty.octopusync.api.GetHeadsetsResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(
                  "net.manaty.octopusync.api.OctopuSync", "GetHeadsets"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  net.manaty.octopusync.api.GetHeadsetsRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  net.manaty.octopusync.api.GetHeadsetsResponse.getDefaultInstance()))
                  .setSchemaDescriptor(new OctopuSyncMethodDescriptorSupplier("GetHeadsets"))
                  .build();
          }
        }
     }
     return getGetHeadsetsMethod;
  }

  private static volatile io.grpc.MethodDescriptor<net.manaty.octopusync.api.CreateSessionRequest,
      net.manaty.octopusync.api.CreateSessionResponse> getCreateSessionMethod;

  public static io.grpc.MethodDescriptor<net.manaty.octopusync.api.CreateSessionRequest,
      net.manaty.octopusync.api.CreateSessionResponse> getCreateSessionMethod() {
    io.grpc.MethodDescriptor<net.manaty.octopusync.api.CreateSessionRequest, net.manaty.octopusync.api.CreateSessionResponse> getCreateSessionMethod;
    if ((getCreateSessionMethod = OctopuSyncGrpc.getCreateSessionMethod) == null) {
      synchronized (OctopuSyncGrpc.class) {
        if ((getCreateSessionMethod = OctopuSyncGrpc.getCreateSessionMethod) == null) {
          OctopuSyncGrpc.getCreateSessionMethod = getCreateSessionMethod = 
              io.grpc.MethodDescriptor.<net.manaty.octopusync.api.CreateSessionRequest, net.manaty.octopusync.api.CreateSessionResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(
                  "net.manaty.octopusync.api.OctopuSync", "CreateSession"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  net.manaty.octopusync.api.CreateSessionRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  net.manaty.octopusync.api.CreateSessionResponse.getDefaultInstance()))
                  .setSchemaDescriptor(new OctopuSyncMethodDescriptorSupplier("CreateSession"))
                  .build();
          }
        }
     }
     return getCreateSessionMethod;
  }

  private static volatile io.grpc.MethodDescriptor<net.manaty.octopusync.api.ClientSyncMessage,
      net.manaty.octopusync.api.ServerSyncMessage> getSyncMethod;

  public static io.grpc.MethodDescriptor<net.manaty.octopusync.api.ClientSyncMessage,
      net.manaty.octopusync.api.ServerSyncMessage> getSyncMethod() {
    io.grpc.MethodDescriptor<net.manaty.octopusync.api.ClientSyncMessage, net.manaty.octopusync.api.ServerSyncMessage> getSyncMethod;
    if ((getSyncMethod = OctopuSyncGrpc.getSyncMethod) == null) {
      synchronized (OctopuSyncGrpc.class) {
        if ((getSyncMethod = OctopuSyncGrpc.getSyncMethod) == null) {
          OctopuSyncGrpc.getSyncMethod = getSyncMethod = 
              io.grpc.MethodDescriptor.<net.manaty.octopusync.api.ClientSyncMessage, net.manaty.octopusync.api.ServerSyncMessage>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.BIDI_STREAMING)
              .setFullMethodName(generateFullMethodName(
                  "net.manaty.octopusync.api.OctopuSync", "Sync"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  net.manaty.octopusync.api.ClientSyncMessage.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  net.manaty.octopusync.api.ServerSyncMessage.getDefaultInstance()))
                  .setSchemaDescriptor(new OctopuSyncMethodDescriptorSupplier("Sync"))
                  .build();
          }
        }
     }
     return getSyncMethod;
  }

  private static volatile io.grpc.MethodDescriptor<net.manaty.octopusync.api.UpdateStateRequest,
      net.manaty.octopusync.api.UpdateStateResponse> getUpdateStateMethod;

  public static io.grpc.MethodDescriptor<net.manaty.octopusync.api.UpdateStateRequest,
      net.manaty.octopusync.api.UpdateStateResponse> getUpdateStateMethod() {
    io.grpc.MethodDescriptor<net.manaty.octopusync.api.UpdateStateRequest, net.manaty.octopusync.api.UpdateStateResponse> getUpdateStateMethod;
    if ((getUpdateStateMethod = OctopuSyncGrpc.getUpdateStateMethod) == null) {
      synchronized (OctopuSyncGrpc.class) {
        if ((getUpdateStateMethod = OctopuSyncGrpc.getUpdateStateMethod) == null) {
          OctopuSyncGrpc.getUpdateStateMethod = getUpdateStateMethod = 
              io.grpc.MethodDescriptor.<net.manaty.octopusync.api.UpdateStateRequest, net.manaty.octopusync.api.UpdateStateResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(
                  "net.manaty.octopusync.api.OctopuSync", "UpdateState"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  net.manaty.octopusync.api.UpdateStateRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  net.manaty.octopusync.api.UpdateStateResponse.getDefaultInstance()))
                  .setSchemaDescriptor(new OctopuSyncMethodDescriptorSupplier("UpdateState"))
                  .build();
          }
        }
     }
     return getUpdateStateMethod;
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
    public void getHeadsets(net.manaty.octopusync.api.GetHeadsetsRequest request,
        io.grpc.stub.StreamObserver<net.manaty.octopusync.api.GetHeadsetsResponse> responseObserver) {
      asyncUnimplementedUnaryCall(getGetHeadsetsMethod(), responseObserver);
    }

    /**
     */
    public void createSession(net.manaty.octopusync.api.CreateSessionRequest request,
        io.grpc.stub.StreamObserver<net.manaty.octopusync.api.CreateSessionResponse> responseObserver) {
      asyncUnimplementedUnaryCall(getCreateSessionMethod(), responseObserver);
    }

    /**
     */
    public io.grpc.stub.StreamObserver<net.manaty.octopusync.api.ClientSyncMessage> sync(
        io.grpc.stub.StreamObserver<net.manaty.octopusync.api.ServerSyncMessage> responseObserver) {
      return asyncUnimplementedStreamingCall(getSyncMethod(), responseObserver);
    }

    /**
     */
    public void updateState(net.manaty.octopusync.api.UpdateStateRequest request,
        io.grpc.stub.StreamObserver<net.manaty.octopusync.api.UpdateStateResponse> responseObserver) {
      asyncUnimplementedUnaryCall(getUpdateStateMethod(), responseObserver);
    }

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
          .addMethod(
            getGetHeadsetsMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                net.manaty.octopusync.api.GetHeadsetsRequest,
                net.manaty.octopusync.api.GetHeadsetsResponse>(
                  this, METHODID_GET_HEADSETS)))
          .addMethod(
            getCreateSessionMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                net.manaty.octopusync.api.CreateSessionRequest,
                net.manaty.octopusync.api.CreateSessionResponse>(
                  this, METHODID_CREATE_SESSION)))
          .addMethod(
            getSyncMethod(),
            asyncBidiStreamingCall(
              new MethodHandlers<
                net.manaty.octopusync.api.ClientSyncMessage,
                net.manaty.octopusync.api.ServerSyncMessage>(
                  this, METHODID_SYNC)))
          .addMethod(
            getUpdateStateMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                net.manaty.octopusync.api.UpdateStateRequest,
                net.manaty.octopusync.api.UpdateStateResponse>(
                  this, METHODID_UPDATE_STATE)))
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
    public void getHeadsets(net.manaty.octopusync.api.GetHeadsetsRequest request,
        io.grpc.stub.StreamObserver<net.manaty.octopusync.api.GetHeadsetsResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getGetHeadsetsMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void createSession(net.manaty.octopusync.api.CreateSessionRequest request,
        io.grpc.stub.StreamObserver<net.manaty.octopusync.api.CreateSessionResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getCreateSessionMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public io.grpc.stub.StreamObserver<net.manaty.octopusync.api.ClientSyncMessage> sync(
        io.grpc.stub.StreamObserver<net.manaty.octopusync.api.ServerSyncMessage> responseObserver) {
      return asyncBidiStreamingCall(
          getChannel().newCall(getSyncMethod(), getCallOptions()), responseObserver);
    }

    /**
     */
    public void updateState(net.manaty.octopusync.api.UpdateStateRequest request,
        io.grpc.stub.StreamObserver<net.manaty.octopusync.api.UpdateStateResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getUpdateStateMethod(), getCallOptions()), request, responseObserver);
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
    public net.manaty.octopusync.api.GetHeadsetsResponse getHeadsets(net.manaty.octopusync.api.GetHeadsetsRequest request) {
      return blockingUnaryCall(
          getChannel(), getGetHeadsetsMethod(), getCallOptions(), request);
    }

    /**
     */
    public net.manaty.octopusync.api.CreateSessionResponse createSession(net.manaty.octopusync.api.CreateSessionRequest request) {
      return blockingUnaryCall(
          getChannel(), getCreateSessionMethod(), getCallOptions(), request);
    }

    /**
     */
    public net.manaty.octopusync.api.UpdateStateResponse updateState(net.manaty.octopusync.api.UpdateStateRequest request) {
      return blockingUnaryCall(
          getChannel(), getUpdateStateMethod(), getCallOptions(), request);
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
    public com.google.common.util.concurrent.ListenableFuture<net.manaty.octopusync.api.GetHeadsetsResponse> getHeadsets(
        net.manaty.octopusync.api.GetHeadsetsRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getGetHeadsetsMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<net.manaty.octopusync.api.CreateSessionResponse> createSession(
        net.manaty.octopusync.api.CreateSessionRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getCreateSessionMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<net.manaty.octopusync.api.UpdateStateResponse> updateState(
        net.manaty.octopusync.api.UpdateStateRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getUpdateStateMethod(), getCallOptions()), request);
    }
  }

  /**
   */
  public static abstract class OctopuSyncVertxImplBase implements io.grpc.BindableService {

    /**
     */
    public void getHeadsets(net.manaty.octopusync.api.GetHeadsetsRequest request,
        io.vertx.core.Future<net.manaty.octopusync.api.GetHeadsetsResponse> response) {
      asyncUnimplementedUnaryCall(getGetHeadsetsMethod(), OctopuSyncGrpc.toObserver(response.completer()));
    }

    /**
     */
    public void createSession(net.manaty.octopusync.api.CreateSessionRequest request,
        io.vertx.core.Future<net.manaty.octopusync.api.CreateSessionResponse> response) {
      asyncUnimplementedUnaryCall(getCreateSessionMethod(), OctopuSyncGrpc.toObserver(response.completer()));
    }

    /**
     */
    public void sync(
        io.vertx.grpc.GrpcBidiExchange<net.manaty.octopusync.api.ClientSyncMessage, net.manaty.octopusync.api.ServerSyncMessage> exchange) {
      exchange.setReadObserver(asyncUnimplementedStreamingCall(getSyncMethod(), exchange.writeObserver()));
    }

    /**
     */
    public void updateState(net.manaty.octopusync.api.UpdateStateRequest request,
        io.vertx.core.Future<net.manaty.octopusync.api.UpdateStateResponse> response) {
      asyncUnimplementedUnaryCall(getUpdateStateMethod(), OctopuSyncGrpc.toObserver(response.completer()));
    }

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
          .addMethod(
            getGetHeadsetsMethod(),
            asyncUnaryCall(
              new VertxMethodHandlers<
                net.manaty.octopusync.api.GetHeadsetsRequest,
                net.manaty.octopusync.api.GetHeadsetsResponse>(
                  this, METHODID_GET_HEADSETS)))
          .addMethod(
            getCreateSessionMethod(),
            asyncUnaryCall(
              new VertxMethodHandlers<
                net.manaty.octopusync.api.CreateSessionRequest,
                net.manaty.octopusync.api.CreateSessionResponse>(
                  this, METHODID_CREATE_SESSION)))
          .addMethod(
            getSyncMethod(),
            asyncBidiStreamingCall(
              new VertxMethodHandlers<
                net.manaty.octopusync.api.ClientSyncMessage,
                net.manaty.octopusync.api.ServerSyncMessage>(
                  this, METHODID_SYNC)))
          .addMethod(
            getUpdateStateMethod(),
            asyncUnaryCall(
              new VertxMethodHandlers<
                net.manaty.octopusync.api.UpdateStateRequest,
                net.manaty.octopusync.api.UpdateStateResponse>(
                  this, METHODID_UPDATE_STATE)))
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
    public void getHeadsets(net.manaty.octopusync.api.GetHeadsetsRequest request,
        io.vertx.core.Handler<io.vertx.core.AsyncResult<net.manaty.octopusync.api.GetHeadsetsResponse>> response) {
      asyncUnaryCall(
          getChannel().newCall(getGetHeadsetsMethod(), getCallOptions()), request, OctopuSyncGrpc.toObserver(response));
    }

    /**
     */
    public void createSession(net.manaty.octopusync.api.CreateSessionRequest request,
        io.vertx.core.Handler<io.vertx.core.AsyncResult<net.manaty.octopusync.api.CreateSessionResponse>> response) {
      asyncUnaryCall(
          getChannel().newCall(getCreateSessionMethod(), getCallOptions()), request, OctopuSyncGrpc.toObserver(response));
    }

    /**
     */
    public void sync(io.vertx.core.Handler<
        io.vertx.grpc.GrpcBidiExchange<net.manaty.octopusync.api.ServerSyncMessage, net.manaty.octopusync.api.ClientSyncMessage>> handler) {
      final io.vertx.grpc.GrpcReadStream<net.manaty.octopusync.api.ServerSyncMessage> readStream =
          io.vertx.grpc.GrpcReadStream.<net.manaty.octopusync.api.ServerSyncMessage>create();

      handler.handle(io.vertx.grpc.GrpcBidiExchange.create(readStream, asyncBidiStreamingCall(
          getChannel().newCall(getSyncMethod(), getCallOptions()), readStream.readObserver())));
    }

    /**
     */
    public void updateState(net.manaty.octopusync.api.UpdateStateRequest request,
        io.vertx.core.Handler<io.vertx.core.AsyncResult<net.manaty.octopusync.api.UpdateStateResponse>> response) {
      asyncUnaryCall(
          getChannel().newCall(getUpdateStateMethod(), getCallOptions()), request, OctopuSyncGrpc.toObserver(response));
    }
  }

  private static final int METHODID_GET_HEADSETS = 0;
  private static final int METHODID_CREATE_SESSION = 1;
  private static final int METHODID_UPDATE_STATE = 2;
  private static final int METHODID_SYNC = 3;

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
        case METHODID_GET_HEADSETS:
          serviceImpl.getHeadsets((net.manaty.octopusync.api.GetHeadsetsRequest) request,
              (io.grpc.stub.StreamObserver<net.manaty.octopusync.api.GetHeadsetsResponse>) responseObserver);
          break;
        case METHODID_CREATE_SESSION:
          serviceImpl.createSession((net.manaty.octopusync.api.CreateSessionRequest) request,
              (io.grpc.stub.StreamObserver<net.manaty.octopusync.api.CreateSessionResponse>) responseObserver);
          break;
        case METHODID_UPDATE_STATE:
          serviceImpl.updateState((net.manaty.octopusync.api.UpdateStateRequest) request,
              (io.grpc.stub.StreamObserver<net.manaty.octopusync.api.UpdateStateResponse>) responseObserver);
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
        case METHODID_SYNC:
          return (io.grpc.stub.StreamObserver<Req>) serviceImpl.sync(
              (io.grpc.stub.StreamObserver<net.manaty.octopusync.api.ServerSyncMessage>) responseObserver);
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
        case METHODID_GET_HEADSETS:
          serviceImpl.getHeadsets((net.manaty.octopusync.api.GetHeadsetsRequest) request,
              (io.vertx.core.Future<net.manaty.octopusync.api.GetHeadsetsResponse>) io.vertx.core.Future.<net.manaty.octopusync.api.GetHeadsetsResponse>future().setHandler(ar -> {
                if (ar.succeeded()) {
                  ((io.grpc.stub.StreamObserver<net.manaty.octopusync.api.GetHeadsetsResponse>) responseObserver).onNext(ar.result());
                  responseObserver.onCompleted();
                } else {
                  responseObserver.onError(ar.cause());
                }
              }));
          break;
        case METHODID_CREATE_SESSION:
          serviceImpl.createSession((net.manaty.octopusync.api.CreateSessionRequest) request,
              (io.vertx.core.Future<net.manaty.octopusync.api.CreateSessionResponse>) io.vertx.core.Future.<net.manaty.octopusync.api.CreateSessionResponse>future().setHandler(ar -> {
                if (ar.succeeded()) {
                  ((io.grpc.stub.StreamObserver<net.manaty.octopusync.api.CreateSessionResponse>) responseObserver).onNext(ar.result());
                  responseObserver.onCompleted();
                } else {
                  responseObserver.onError(ar.cause());
                }
              }));
          break;
        case METHODID_UPDATE_STATE:
          serviceImpl.updateState((net.manaty.octopusync.api.UpdateStateRequest) request,
              (io.vertx.core.Future<net.manaty.octopusync.api.UpdateStateResponse>) io.vertx.core.Future.<net.manaty.octopusync.api.UpdateStateResponse>future().setHandler(ar -> {
                if (ar.succeeded()) {
                  ((io.grpc.stub.StreamObserver<net.manaty.octopusync.api.UpdateStateResponse>) responseObserver).onNext(ar.result());
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
        case METHODID_SYNC:
          io.vertx.grpc.GrpcReadStream<net.manaty.octopusync.api.ClientSyncMessage> request2 = io.vertx.grpc.GrpcReadStream.<net.manaty.octopusync.api.ClientSyncMessage>create();
          serviceImpl.sync(
             io.vertx.grpc.GrpcBidiExchange.<net.manaty.octopusync.api.ClientSyncMessage, net.manaty.octopusync.api.ServerSyncMessage>create(
               request2,
               (io.grpc.stub.StreamObserver<net.manaty.octopusync.api.ServerSyncMessage>) responseObserver));
          return (io.grpc.stub.StreamObserver<Req>) request2.readObserver();
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
              .addMethod(getGetHeadsetsMethod())
              .addMethod(getCreateSessionMethod())
              .addMethod(getSyncMethod())
              .addMethod(getUpdateStateMethod())
              .build();
        }
      }
    }
    return result;
  }
}
