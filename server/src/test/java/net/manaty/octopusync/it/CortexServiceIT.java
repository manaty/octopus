package net.manaty.octopusync.it;

import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import net.manaty.octopusync.it.fixture.CortexTestBase;
import net.manaty.octopusync.it.fixture.emotiv.TestCortexCredentials;
import net.manaty.octopusync.it.fixture.emotiv.TestCortexResources;
import net.manaty.octopusync.service.emotiv.CortexService;
import net.manaty.octopusync.service.emotiv.CortexServiceImpl;
import net.manaty.octopusync.service.emotiv.EmotivCredentials;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(VertxUnitRunner.class)
public class CortexServiceIT extends CortexTestBase {

    private CortexService cortexService;

    @Before
    public void setUp() {
        super.setUp();

        TestCortexCredentials credentials = TestCortexResources.loadCredentials().get(1);
        EmotivCredentials emotivCredentials = new EmotivCredentials(
                credentials.getUsername(),
                credentials.getPassword(),
                credentials.getClientId(),
                credentials.getClientSecret(),
                null);

        cortexService = new CortexServiceImpl(vertx, client,
                emotivCredentials, TestCortexResources.loadHeadsetIdsToCodes().keySet());
    }

    @Test
    public void test(TestContext context) {
        Async async = context.async();

        cortexService.startCapture()
                .subscribe();
    }
}
