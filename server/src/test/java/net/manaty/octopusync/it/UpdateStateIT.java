package net.manaty.octopusync.it;

import net.manaty.octopusync.api.UpdateStateRequest;
import net.manaty.octopusync.api.UpdateStateResponse;
import org.junit.Test;

public class UpdateStateIT extends ServerTestBase {

    @Test
    public void test() {
        UpdateStateResponse response = server.blockingGrpcStub().updateState(UpdateStateRequest.getDefaultInstance());
    }
}
