package net.manaty.octopusync.it;

import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import net.manaty.octopusync.it.fixture.CortexTestBase;
import net.manaty.octopusync.it.fixture.emotiv.TestCortexCredentials;
import net.manaty.octopusync.it.fixture.emotiv.TestCortexResources;
import net.manaty.octopusync.model.EegEvent;
import net.manaty.octopusync.service.emotiv.CortexService;
import net.manaty.octopusync.service.emotiv.CortexServiceImpl;
import net.manaty.octopusync.service.emotiv.EmotivCredentials;
import net.manaty.octopusync.service.emotiv.event.CortexEventVisitor;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@RunWith(VertxUnitRunner.class)
public class CortexServiceIT extends CortexTestBase {

    private Set<String> headsetIds;
    private CortexService cortexService;

    @Before
    public void setUp() {
        super.setUp();

        headsetIds = TestCortexResources.loadHeadsetIdsToCodes().keySet();

        TestCortexCredentials credentials = TestCortexResources.loadCredentials().get(1);
        EmotivCredentials emotivCredentials = new EmotivCredentials(
                credentials.getUsername(),
                credentials.getPassword(),
                credentials.getClientId(),
                credentials.getClientSecret(),
                credentials.getAppId(),
                null);

        cortexService = new CortexServiceImpl(vertx, client, emotivCredentials, headsetIds, Collections.emptySet());
    }

    @Test
    public void test(TestContext context) {
        Set<String> subscriptionIds = ConcurrentHashMap.newKeySet();
        Async async = context.async();

        cortexService.startCapture()
                .forEach(event -> {
                    event.visitEvent(new CortexEventVisitor() {
                        @Override
                        public void visitEegEvent(EegEvent event) {
                            subscriptionIds.add(event.getSid());
                            if (subscriptionIds.size() == headsetIds.size()) {
                                async.complete();
                            }
                        }
                    });
                });
    }
}
