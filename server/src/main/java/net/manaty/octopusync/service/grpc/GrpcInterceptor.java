package net.manaty.octopusync.service.grpc;

import io.grpc.*;

import java.net.SocketAddress;

public class GrpcInterceptor implements ServerInterceptor {

    private static final ThreadLocal<SocketAddress> REMOTE_ADDR = new ThreadLocal<>();

    public static SocketAddress getRemoteAddr() {
        return REMOTE_ADDR.get();
    }

    @Override
    public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(
            ServerCall<ReqT, RespT> call, Metadata headers, ServerCallHandler<ReqT, RespT> next) {

        REMOTE_ADDR.set(call.getAttributes().get(Grpc.TRANSPORT_ATTR_REMOTE_ADDR));

        ServerCall.Listener<ReqT> listener = next.startCall(call, headers);
        return new ForwardingServerCallListener.SimpleForwardingServerCallListener<ReqT>(listener) {
            @Override
            public void onMessage(ReqT message) {
                super.onMessage(message);
            }
        };
    }
}
