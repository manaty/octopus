package net.manaty.octopusync.model;

import com.google.common.base.MoreObjects;
import net.manaty.octopusync.service.emotiv.event.CortexEvent;
import net.manaty.octopusync.service.emotiv.event.CortexEventVisitor;

public class MotEvent implements CortexEvent, Timestamped {

    private String headsetId;
    private String sid;
    private long time;
    private long counter;
    private double gyrox;
    private double gyroy;
    private double gyroz;
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

    public double getGyrox() {
        return gyrox;
    }

    public void setGyrox(double gyrox) {
        this.gyrox = gyrox;
    }

    public double getGyroy() {
        return gyroy;
    }

    public void setGyroy(double gyroy) {
        this.gyroy = gyroy;
    }

    public double getGyroz() {
        return gyroz;
    }

    public void setGyroz(double gyroz) {
        this.gyroz = gyroz;
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
                .add("gyrox", gyrox)
                .add("gyroy", gyroy)
                .add("gyroz", gyroz)
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
