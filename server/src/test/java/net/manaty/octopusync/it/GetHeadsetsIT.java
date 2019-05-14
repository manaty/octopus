package net.manaty.octopusync.it;

import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import net.manaty.octopusync.api.GetHeadsetsRequest;
import net.manaty.octopusync.api.Headset;
import net.manaty.octopusync.it.fixture.ServerTestBase;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

@RunWith(VertxUnitRunner.class)
public class GetHeadsetsIT extends ServerTestBase {

    @Test
    public void testGetHeadsets(TestContext context) {
        Async async = context.async();
        server.vertxStub().getHeadsets(GetHeadsetsRequest.getDefaultInstance(), ar -> {
            if (ar.succeeded()) {
                List<Headset> headsets = ar.result().getHeadsetsList();
                context.assertEquals(4, headsets.size());
                context.assertTrue(containsHeadset(headsets, "headset1", "H1"));
                context.assertTrue(containsHeadset(headsets, "headset2", "H2"));
                context.assertTrue(containsHeadset(headsets, "headset3", "H3"));
                context.assertTrue(containsHeadset(headsets, "headset4", "H4"));
                async.complete();
            } else {
                context.fail(new IllegalStateException("Unexpected error", ar.cause()));
            }
        });
    }

    private boolean containsHeadset(List<Headset> headsets, String id, String code) {
        for (Headset headset : headsets) {
            if (headset.getId().equals(id) && headset.getCode().equals(code)) {
                return true;
            }
        }
        return false;
    }
}
