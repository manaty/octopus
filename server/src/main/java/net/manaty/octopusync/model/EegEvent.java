package net.manaty.octopusync.model;

import com.google.common.base.MoreObjects;
import net.manaty.octopusync.service.emotiv.event.CortexEvent;
import net.manaty.octopusync.service.emotiv.event.CortexEventVisitor;

public class EegEvent implements CortexEvent {

    private String sid;
    private long time;
    private long counter;
    private boolean interpolated;
    private double signalQuality;
    private double af3;
    private double f7;
    private double f3;
    private double fc5;
    private double t7;
    private double p7;
    private double o1;
    private double o2;
    private double p8;
    private double t8;
    private double fc6;
    private double f4;
    private double f8;
    private double af4;
    private int markerHardware;
    private int marker;

    public String getSid() {
        return sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
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

    public double getF7() {
        return f7;
    }

    public void setF7(double f7) {
        this.f7 = f7;
    }

    public double getF3() {
        return f3;
    }

    public void setF3(double f3) {
        this.f3 = f3;
    }

    public double getFc5() {
        return fc5;
    }

    public void setFc5(double fc5) {
        this.fc5 = fc5;
    }

    public double getT7() {
        return t7;
    }

    public void setT7(double t7) {
        this.t7 = t7;
    }

    public double getP7() {
        return p7;
    }

    public void setP7(double p7) {
        this.p7 = p7;
    }

    public double getO1() {
        return o1;
    }

    public void setO1(double o1) {
        this.o1 = o1;
    }

    public double getO2() {
        return o2;
    }

    public void setO2(double o2) {
        this.o2 = o2;
    }

    public double getP8() {
        return p8;
    }

    public void setP8(double p8) {
        this.p8 = p8;
    }

    public double getT8() {
        return t8;
    }

    public void setT8(double t8) {
        this.t8 = t8;
    }

    public double getFc6() {
        return fc6;
    }

    public void setFc6(double fc6) {
        this.fc6 = fc6;
    }

    public double getF4() {
        return f4;
    }

    public void setF4(double f4) {
        this.f4 = f4;
    }

    public double getF8() {
        return f8;
    }

    public void setF8(double f8) {
        this.f8 = f8;
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

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("sid", sid)
                .add("time", time)
                .add("counter", counter)
                .add("interpolated", interpolated)
                .add("signalQuality", signalQuality)
                .add("af3", af3)
                .add("f7", f7)
                .add("f3", f3)
                .add("fc5", fc5)
                .add("t7", t7)
                .add("p7", p7)
                .add("o1", o1)
                .add("o2", o2)
                .add("p8", p8)
                .add("t8", t8)
                .add("fc6", fc6)
                .add("f4", f4)
                .add("f8", f8)
                .add("af4", af4)
                .add("markerHardware", markerHardware)
                .add("marker", marker)
                .toString();
    }
}
