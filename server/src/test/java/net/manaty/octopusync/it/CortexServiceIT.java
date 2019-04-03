package net.manaty.octopusync.it;

import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import net.manaty.octopusync.it.fixture.CortexTestBase;
import net.manaty.octopusync.service.emotiv.CortexService;
import net.manaty.octopusync.service.emotiv.EmotivCredentials;
import org.junit.Before;
import org.junit.Test;

import java.util.Collections;

public class CortexServiceIT extends CortexTestBase {

    private CortexService cortexService;

    @Before
    public void setUp() {
        super.setUp();
        String username = randomString();
        String clientId = randomString();
        EmotivCredentials credentials = new EmotivCredentials(
                username, "password", clientId, "clientSecret", null);
        this.cortexService = new CortexService(vertx, client, credentials, Collections.emptyMap());
    }

    @Test
    public void test(TestContext context) {
        Async async = context.async();

        cortexService.startCapture()
                .subscribe(() -> {}, context::fail);
    }
}
