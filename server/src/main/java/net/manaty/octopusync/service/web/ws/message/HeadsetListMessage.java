package net.manaty.octopusync.service.web.ws.message;

import com.google.common.base.MoreObjects;
import net.manaty.octopusync.service.web.ws.JsonEncoder;

import javax.annotation.Nullable;
import java.util.Map;

public class HeadsetListMessage extends BaseMessage {

    public static class Encoder extends JsonEncoder<HeadsetListMessage> {
    }

    private final Map<String, Status> statusByHeadsetId;

    public HeadsetListMessage(long id, Map<String, Status> statusByHeadsetId) {
        super(id, "headsets");
        this.statusByHeadsetId = statusByHeadsetId;
    }

    public Map<String, Status> getStatusByHeadsetId() {
        return statusByHeadsetId;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("id", id)
                .add("type", type)
                .add("statusByHeadsetId", statusByHeadsetId)
                .toString();
    }

    public static class Status {

        private final boolean connected;
        private final boolean clientSessionCreated;
        private @Nullable final Info info;

        public Status(boolean connected, boolean clientSessionCreated, Info info) {
            this.connected = connected;
            this.clientSessionCreated = clientSessionCreated;
            this.info = info;
        }

        public boolean isConnected() {
            return connected;
        }

        public boolean isClientSessionCreated() {
            return clientSessionCreated;
        }

        public Info getInfo() {
            return info;
        }

        @Override
        public String toString() {
            return MoreObjects.toStringHelper(this)
                    .add("connected", connected)
                    .add("clientSessionCreated", clientSessionCreated)
                    .add("info", info)
                    .toString();
        }

        public static class Info {

            private final int battery;
            private final int signal;
            private final double af3;
            private final double f7;
            private final double f3;
            private final double fc5;
            private final double t7;
            private final double p7;
            private final double o1;
            private final double o2;
            private final double p8;
            private final double t8;
            private final double fc6;
            private final double f4;
            private final double f8;
            private final double af4;

            public Info(int battery, int signal, double af3, double f7, double f3, double fc5, double t7,
                        double p7, double o1, double o2, double p8, double t8, double fc6, double f4,
                        double f8, double af4) {
                this.battery = battery;
                this.signal = signal;
                this.af3 = af3;
                this.f7 = f7;
                this.f3 = f3;
                this.fc5 = fc5;
                this.t7 = t7;
                this.p7 = p7;
                this.o1 = o1;
                this.o2 = o2;
                this.p8 = p8;
                this.t8 = t8;
                this.fc6 = fc6;
                this.f4 = f4;
                this.f8 = f8;
                this.af4 = af4;
            }

            public int getBattery() {
                return battery;
            }

            public int getSignal() {
                return signal;
            }

            public double getAf3() {
                return af3;
            }

            public double getF7() {
                return f7;
            }

            public double getF3() {
                return f3;
            }

            public double getFc5() {
                return fc5;
            }

            public double getT7() {
                return t7;
            }

            public double getP7() {
                return p7;
            }

            public double getO1() {
                return o1;
            }

            public double getO2() {
                return o2;
            }

            public double getP8() {
                return p8;
            }

            public double getT8() {
                return t8;
            }

            public double getFc6() {
                return fc6;
            }

            public double getF4() {
                return f4;
            }

            public double getF8() {
                return f8;
            }

            public double getAf4() {
                return af4;
            }

            @Override
            public String toString() {
                return MoreObjects.toStringHelper(this)
                        .add("battery", battery)
                        .add("signal", signal)
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
                        .toString();
            }
        }
    }
}
