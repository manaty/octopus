package net.manaty.octopusync.service.s2s;

import net.manaty.octopusync.model.S2STimeSyncResult;
import net.manaty.octopusync.service.common.NetworkUtils;

import java.net.InetSocketAddress;
import java.util.Objects;

public class SyncResultBuilder {

    public static SyncResultBuilder builder(InetSocketAddress localAddress, InetSocketAddress remoteAddress) {
        return new SyncResultBuilder(localAddress, remoteAddress, 0);
    }

    private final InetSocketAddress localAddress;
    private final InetSocketAddress remoteAddress;
    private final long round;

    private SyncResultBuilder(InetSocketAddress localAddress, InetSocketAddress remoteAddress, long round) {
        this.localAddress = Objects.requireNonNull(localAddress);
        this.remoteAddress = Objects.requireNonNull(remoteAddress);
        this.round = round;
    }

    public InetSocketAddress getLocalAddress() {
        return localAddress;
    }

    public InetSocketAddress getRemoteAddress() {
        return remoteAddress;
    }

    public SyncResultBuilder newBuilderForRound(long round) {
        return new SyncResultBuilder(localAddress, remoteAddress, round);
    }

    public long getRound() {
        return round;
    }

    public S2STimeSyncResult ok(long finished, long delay) {
        return buildResult(finished, delay, null);
    }

    public S2STimeSyncResult failure(long finished, Throwable error) {
        return buildResult(finished, 0, error);
    }

    private S2STimeSyncResult buildResult(long finished, long delay, Throwable error) {
        String localAddress = NetworkUtils.stringifyAddress(this.localAddress);
        String remoteAddress = NetworkUtils.stringifyAddress(this.remoteAddress);
        String errorMessage = (error == null) ? null : buildErrorMessage(error);
        return new S2STimeSyncResult(localAddress, remoteAddress, round, finished, delay, errorMessage);
    }

    private static String buildErrorMessage(Throwable error) {
        StringBuilder buf = new StringBuilder();
        do {
            if (buf.length() > 0) {
                buf.append(" => ");
            }
            buf.append(error.getClass().getName());
            if (error.getMessage() != null) {
                buf.append(": ");
                buf.append(error.getMessage());
            }
            error = error.getCause();
        } while (error != null);
        return buf.toString();
    }
}
