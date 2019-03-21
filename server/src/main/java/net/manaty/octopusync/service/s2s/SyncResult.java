package net.manaty.octopusync.service.s2s;

import com.google.common.base.MoreObjects;

import java.net.InetSocketAddress;

public class SyncResult {

    public static SyncResult ok(InetSocketAddress nodeAddress, long round, long finished, long delay) {
        return new SyncResult(nodeAddress, round, finished, delay, null);
    }

    public static SyncResult failure(InetSocketAddress nodeAddress, long round, long finished, Throwable error) {
        return new SyncResult(nodeAddress, round, finished, 0, error);
    }

    private final InetSocketAddress nodeAddress;
    private final long round;
    private final long finished;

    private final long delay;
    private final Throwable error;

    private SyncResult(InetSocketAddress nodeAddress, long round, long finished, long delay, Throwable error) {
        this.nodeAddress = nodeAddress;
        this.round = round;
        this.finished = finished;
        this.delay = delay;
        this.error = error;
    }

    public InetSocketAddress getNodeAddress() {
        return nodeAddress;
    }

    public long getRound() {
        return round;
    }

    public long getFinished() {
        return finished;
    }

    public long getDelay() {
        return delay;
    }

    public Throwable getError() {
        return error;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("nodeAddress", nodeAddress)
                .add("round", round)
                .add("finished", finished)
                .add("delay", delay)
                .add("error", error)
                .toString();
    }
}
