package net.manaty.octopusync.service.s2s;

import net.manaty.octopusync.model.S2STimeSyncResult;
import net.manaty.octopusync.service.common.NetworkUtils;
import net.manaty.octopusync.service.sync.SyncResultBuilder;

import javax.annotation.Nullable;
import java.net.InetSocketAddress;
import java.util.Objects;

public class S2STimeSyncResultBuilder extends SyncResultBuilder<S2STimeSyncResultBuilder, S2STimeSyncResult> {

    public static S2STimeSyncResultBuilder builder(InetSocketAddress localAddress, InetSocketAddress remoteAddress) {
        return new S2STimeSyncResultBuilder(localAddress, remoteAddress, 0);
    }

    private final InetSocketAddress localAddress;
    private final InetSocketAddress remoteAddress;

    private S2STimeSyncResultBuilder(InetSocketAddress localAddress, InetSocketAddress remoteAddress, long round) {
        super(round);
        this.localAddress = Objects.requireNonNull(localAddress);
        this.remoteAddress = Objects.requireNonNull(remoteAddress);
    }

    public InetSocketAddress getLocalAddress() {
        return localAddress;
    }

    public InetSocketAddress getRemoteAddress() {
        return remoteAddress;
    }

    public S2STimeSyncResultBuilder newBuilderForRound(long round) {
        return new S2STimeSyncResultBuilder(localAddress, remoteAddress, round);
    }

    @Override
    protected S2STimeSyncResult buildResult(long round, long finished, long delay, @Nullable String errorMessage) {
        String localAddress = NetworkUtils.stringifyAddress(this.localAddress);
        String remoteAddress = NetworkUtils.stringifyAddress(this.remoteAddress);
        return new S2STimeSyncResult(localAddress, remoteAddress, round, finished, delay, errorMessage);
    }

    @Override
    public String getTargetDescription() {
        return remoteAddress.toString();
    }
}
