package net.manaty.octopusync.service.web.ws.message;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.MoreObjects;
import net.manaty.octopusync.service.web.ws.JsonEncoder;

import java.util.Set;

public class SlaveListMessage extends BaseMessage {

    public static class Encoder extends JsonEncoder<SlaveListMessage> {
    }

    private final Set<String> slaveAddresses;

    public SlaveListMessage(long id, Set<String> slaveAddresses) {
        super(id, "slaves");
        this.slaveAddresses = slaveAddresses;
    }

    @JsonProperty("slaves")
    public Set<String> getSlaveAddresses() {
        return slaveAddresses;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("id", id)
                .add("type", type)
                .add("slaveAddresses", slaveAddresses)
                .toString();
    }
}
