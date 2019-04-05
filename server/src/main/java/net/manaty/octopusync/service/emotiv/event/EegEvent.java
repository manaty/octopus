package net.manaty.octopusync.service.emotiv.event;

public class EegEvent implements CortexEvent {

    private String sid;
    private double time;
    private long counter;
    private boolean interpolated;
    private double signalQuality;
    private double af3;
    private double t7;
    private double pz;
    private double t8;
    private double af4;
    private int markerHardware;
    private int marker;

    public String getSid() {
        return sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }

    public double getTime() {
        return time;
    }

    public void setTime(double time) {
        this.time = time;
    }

    public long getCounter() {
        return counter;
    }

    public void setCounter(long counter) {
        this.counter = counter;
    }

    public boolean isInterpolated() {
        return interpolated;
    }

    public void setInterpolated(boolean interpolated) {
        this.interpolated = interpolated;
    }

    public double getSignalQuality() {
        return signalQuality;
    }

    public void setSignalQuality(double signalQuality) {
        this.signalQuality = signalQuality;
    }

    public double getAf3() {
        return af3;
    }

    public void setAf3(double af3) {
        this.af3 = af3;
    }

    public double getT7() {
        return t7;
    }

    public void setT7(double t7) {
        this.t7 = t7;
    }

    public double getPz() {
        return pz;
    }

    public void setPz(double pz) {
        this.pz = pz;
    }

    public double getT8() {
        return t8;
    }

    public void setT8(double t8) {
        this.t8 = t8;
    }

    public double getAf4() {
        return af4;
    }

    public void setAf4(double af4) {
        this.af4 = af4;
    }

    public int getMarkerHardware() {
        return markerHardware;
    }

    public void setMarkerHardware(int markerHardware) {
        this.markerHardware = markerHardware;
    }

    public int getMarker() {
        return marker;
    }

    public void setMarker(int marker) {
        this.marker = marker;
    }

    @Override
    public void visitEvent(CortexEventVisitor visitor) {
        visitor.visitEegEvent(this);
    }
}
