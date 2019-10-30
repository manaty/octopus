package net.manaty.octopusync.model;

import com.google.common.base.MoreObjects;
import net.manaty.octopusync.service.sync.SyncMeasurement;

import java.util.List;

public class S2STimeSyncResult implements Timestamped {

    private final String localAddress;
    private final String remoteAddress;
    private final long round;
    private final List<SyncMeasurement> measurements;
    private final long finished;
    private final long delay;
    private final String error;

    public S2STimeSyncResult(
            String localAddress, String remoteAddress,
            long round,
            List<SyncMeasurement> measurements,
            long finished, long delay, String error) {

        this.localAddress = localAddress;
        this.remoteAddress = remoteAddress;
        this.round = round;
        this.measurements = measurements;
        this.finished = finished;
        this.delay = delay;
        this.error = error;
    }

    public String getLocalAddress() {
        return localAddress;
    }

    public String getRemoteAddress() {
        return remoteAddress;
    }

    public long getRound() {
        return round;
    }

    public List<SyncMeasurement> getMeasurements() {
        return measurements;
    }

    public long getFinished() {
        return finished;
    }

    public long getDelay() {
        return delay;
    }

    public String getError() {
        return error;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("localAddress", localAddress)
                .add("remoteAddress", remoteAddress)
                .add("round", round)
                .add("finished", finished)
                .add("delay", delay)
                .add("error", error)
                .toString();
    }

    @Override
    public long timestamp() {
        return finished;
    }
}
