package net.manaty.octopusync.service.web.admin.message;

import com.google.common.base.MoreObjects;
import net.manaty.octopusync.service.web.admin.JsonEncoder;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;

public class ServerListMessage extends BaseMessage {

    public static class Encoder extends JsonEncoder<ServerListMessage> {
    }

    private final Map<String, List<SyncResult>> syncResultsByAddress;

    public ServerListMessage(long id, Map<String, List<SyncResult>> syncResultsByAddress) {
        super(id, "servers");
        this.syncResultsByAddress = syncResultsByAddress;
    }

    public Map<String, List<SyncResult>> getSyncResultsByAddress() {
        return syncResultsByAddress;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("id", id)
                .add("type", type)
                .add("syncResultsByAddress", syncResultsByAddress)
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
