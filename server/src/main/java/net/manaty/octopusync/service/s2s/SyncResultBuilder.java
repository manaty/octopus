package net.manaty.octopusync.service.s2s;

import net.manaty.octopusync.model.SyncResult;
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

    public SyncResult ok(long finished, long delay) {
        return buildResult(finished, delay, null);
    }

    public SyncResult failure(long finished, Throwable error) {
        return buildResult(finished, 0, error);
    }

    private SyncResult buildResult(long finished, long delay, Throwable error) {
        String localAddress = NetworkUtils.stringifyAddress(this.localAddress);
        String remoteAddress = NetworkUtils.stringifyAddress(this.remoteAddress);
        return new SyncResult(localAddress, remoteAddress, round, finished, delay, error);
    }
}
