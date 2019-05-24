package net.manaty.octopusync.service.web.ws.message;

import com.google.common.base.MoreObjects;
import net.manaty.octopusync.service.web.ws.JsonEncoder;

import javax.annotation.Nullable;

public class MasterSyncResultMessage extends BaseMessage {

    public static class Encoder extends JsonEncoder<MasterSyncResultMessage> {
    }

    private final SyncResult syncResult;

    public MasterSyncResultMessage(long id, SyncResult syncResult) {
        super(id, "mastersync");
        this.syncResult = syncResult;
    }

    public SyncResult getSyncResult() {
        return syncResult;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("id", id)
                .add("type", type)
                .add("syncResult", syncResult)
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
                    .toString();
        }
    }
}
