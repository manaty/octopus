package net.manaty.octopusync.it;

import net.manaty.octopusync.api.SendClickRequest;
import net.manaty.octopusync.api.SendClickResponse;
import org.junit.Test;

public class SendClickIT extends ServerTestBase {

    @Test
    public void test() {
        SendClickResponse response = server.blockingGrpcStub().sendClick(SendClickRequest.getDefaultInstance());
    }
}
