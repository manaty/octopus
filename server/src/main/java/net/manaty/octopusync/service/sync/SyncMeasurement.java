package net.manaty.octopusync.service.sync;

public class SyncMeasurement {

    private final long seqnum;
    private final long sent;
    private final long received;
    private final long delta;
    private final double mean;
    private final double varianceUnbiased;
    private final double stddev;

    public SyncMeasurement(long seqnum, long sent, long received, long delta, double mean, double varianceUnbiased, double stddev) {
        this.seqnum = seqnum;
        this.sent = sent;
        this.received = received;
        this.delta = delta;
        this.mean = mean;
        this.varianceUnbiased = varianceUnbiased;
        this.stddev = stddev;
    }

    public long getSeqnum() {
        return seqnum;
    }

    public long getSent() {
        return sent;
    }

    public long getReceived() {
        return received;
    }

    public long getDelta() {
        return delta;
    }

    public double getMean() {
        return mean;
    }

    public double getVarianceUnbiased() {
        return varianceUnbiased;
    }

    public double getStddev() {
        return stddev;
    }
}
