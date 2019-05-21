package net.manaty.octopusync.service.web.ws.message;

import com.google.common.base.MoreObjects;
import net.manaty.octopusync.service.web.ws.JsonEncoder;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;

public class ClientListMessage extends BaseMessage {

    public static class Encoder extends JsonEncoder<ClientListMessage> {
    }

    private final Map<String, List<SyncResult>> syncResultsByHeadsetId;

    public ClientListMessage(long id, Map<String, List<SyncResult>> syncResultsByHeadsetId) {
        super(id, "clients");
        this.syncResultsByHeadsetId = syncResultsByHeadsetId;
    }

    public Map<String, List<SyncResult>> getSyncResultsByHeadsetId() {
        return syncResultsByHeadsetId;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("id", id)
                .add("type", type)
                .add("syncResultsByHeadsetId", syncResultsByHeadsetId)
                .toString();
    }

    public static class SyncResult {

        private final long finished;
        private final long delay;
        private final String error;

        public SyncResult(long finished, long delay, @Nullable String error) {
            this.finished = finished;
            this.delay = delay;
            this.error = error;
        }

        public long getFinished() {
            return finished;
        }

        public long getDelay() {
            return delay;
        }

        public @Nullable String getError() {
            return error;
        }

        @Override
        public String toString() {
            return MoreObjects.toStringHelper(this)
                    .add("finished", finished)
                    .add("delay", delay)
                    .add("error", error)
                    .toString();
        }
    }
}
