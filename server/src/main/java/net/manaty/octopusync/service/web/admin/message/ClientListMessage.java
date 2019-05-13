package net.manaty.octopusync.service.web.admin.message;

import com.google.common.base.MoreObjects;
import net.manaty.octopusync.service.web.admin.JsonEncoder;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;

public class ClientListMessage {

    public static class Encoder extends JsonEncoder<ClientListMessage> {
    }

    private final long id;
    private final Map<String, List<SyncResult>> syncResultsByHeadsetId;

    public ClientListMessage(long id, Map<String, List<SyncResult>> syncResultsByHeadsetId) {
        this.id = id;
        this.syncResultsByHeadsetId = syncResultsByHeadsetId;
    }

    public long getId() {
        return id;
    }

    public Map<String, List<SyncResult>> getSyncResultsByHeadsetId() {
        return syncResultsByHeadsetId;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("id", id)
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
