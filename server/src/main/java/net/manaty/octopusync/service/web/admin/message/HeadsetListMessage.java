package net.manaty.octopusync.service.web.admin.message;

import com.google.common.base.MoreObjects;
import net.manaty.octopusync.service.web.admin.JsonEncoder;

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

        public Status(boolean connected, boolean clientSessionCreated) {
            this.connected = connected;
            this.clientSessionCreated = clientSessionCreated;
        }

        public boolean isConnected() {
            return connected;
        }

        public boolean isClientSessionCreated() {
            return clientSessionCreated;
        }

        @Override
        public String toString() {
            return MoreObjects.toStringHelper(this)
                    .add("connected", connected)
                    .add("clientSessionCreated", clientSessionCreated)
                    .toString();
        }
    }
}
