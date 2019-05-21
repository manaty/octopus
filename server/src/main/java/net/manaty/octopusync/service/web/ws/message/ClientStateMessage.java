package net.manaty.octopusync.service.web.ws.message;

import com.google.common.base.MoreObjects;
import net.manaty.octopusync.service.web.ws.JsonEncoder;

import java.util.List;
import java.util.Map;

public class ClientStateMessage extends BaseMessage {

    public static class Encoder extends JsonEncoder<ClientStateMessage> {
    }

    private final Map<String, List<State>> statesByHeadsetId;

    public ClientStateMessage(long id, Map<String, List<State>> statesByHeadsetId) {
        super(id, "clientstates");
        this.statesByHeadsetId = statesByHeadsetId;
    }

    public Map<String, List<State>> getStatesByHeadsetId() {
        return statesByHeadsetId;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("id", id)
                .add("type", type)
                .add("statesByHeadsetId", statesByHeadsetId)
                .toString();
    }

    public static class State {

        private final String state;
        private final long since;

        public State(String state, long since) {
            this.state = state;
            this.since = since;
        }

        public String getState() {
            return state;
        }

        public long getSince() {
            return since;
        }

        @Override
        public String toString() {
            return MoreObjects.toStringHelper(this)
                    .add("state", state)
                    .add("since", since)
                    .toString();
        }
    }
}
