package net.manaty.octopusync.model;

import com.google.common.base.MoreObjects;
import net.manaty.octopusync.service.emotiv.event.CortexEvent;
import net.manaty.octopusync.service.emotiv.event.CortexEventVisitor;

public class MotEvent implements CortexEvent, Timestamped {

    private String headsetId;
    private String sid;
    private long time;
    private long counter;
    private double q0;
    private double q1;
    private double q2;
    private double q3;
    private double accx;
    private double accy;
    private double accz;
    private double magx;
    private double magy;
    private double magz;

    public String getHeadsetId() {
        return headsetId;
    }

    public void setHeadsetId(String headsetId) {
        this.headsetId = headsetId;
    }

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

    public double getQ0() {
        return q0;
    }

    public void setQ0(double q0) {
        this.q0 = q0;
    }

    public double getQ1() {
        return q1;
    }

    public void setQ1(double q1) {
        this.q1 = q1;
    }

    public double getQ2() {
        return q2;
    }

    public void setQ2(double q2) {
        this.q2 = q2;
    }

    public double getQ3() {
        return q3;
    }

    public void setQ3(double q3) {
        this.q3 = q3;
    }

    public double getAccx() {
        return accx;
    }

    public void setAccx(double accx) {
        this.accx = accx;
    }

    public double getAccy() {
        return accy;
    }

    public void setAccy(double accy) {
        this.accy = accy;
    }

    public double getAccz() {
        return accz;
    }

    public void setAccz(double accz) {
        this.accz = accz;
    }

    public double getMagx() {
        return magx;
    }

    public void setMagx(double magx) {
        this.magx = magx;
    }

    public double getMagy() {
        return magy;
    }

    public void setMagy(double magy) {
        this.magy = magy;
    }

    public double getMagz() {
        return magz;
    }

    public void setMagz(double magz) {
        this.magz = magz;
    }

    @Override
    public void visitEvent(CortexEventVisitor visitor) {
        visitor.visitMotEvent(this);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("headsetId", headsetId)
                .add("sid", sid)
                .add("time", time)
                .add("counter", counter)
                .add("q0", q0)
                .add("q1", q1)
                .add("q2", q2)
                .add("q3", q3)
                .add("accx", accx)
                .add("accy", accy)
                .add("accz", accz)
                .add("magx", magx)
                .add("magy", magy)
                .add("magz", magz)
                .toString();
    }

    @Override
    public long timestamp() {
        return time;
    }
}
