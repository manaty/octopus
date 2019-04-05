package net.manaty.octopusync.it;

import io.reactivex.Completable;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.RunTestOnContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import io.vertx.reactivex.core.Vertx;
import net.manaty.octopusync.it.fixture.db.InMemoryStorage;
import net.manaty.octopusync.model.EegEvent;
import net.manaty.octopusync.service.db.CortexEventPersistor;
import net.manaty.octopusync.service.db.CortexEventPersistorImpl;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

import static org.testng.Assert.assertTrue;

@RunWith(VertxUnitRunner.class)
public class CortexEventPersistorTest {

    @Rule
    public RunTestOnContext vertxRule = new RunTestOnContext();

    private Vertx vertx;

    @Before
    public void setUp() {
        vertx = new Vertx(vertxRule.vertx());
    }

    @Test
    public void test(TestContext context) {
        InMemoryStorage storage = new InMemoryStorage(10);
        int batchSize = 10;
        CortexEventPersistor persistor = new CortexEventPersistorImpl(vertx, storage, batchSize);

        int capacity = (int)(batchSize * 500.3); // let the last batch be smaller than the rest
        List<EegEvent> sentEvents = new ArrayList<>(capacity + 1);

        Async async = context.async();
        persistor.start()
                .andThen(Completable.fromAction(() -> {
                    for (int i = 0; i < capacity; i++) {
                        EegEvent event = new EegEvent();
                        persistor.save(event);
                        sentEvents.add(event);
                    }
                }))
                .andThen(persistor.stop())
                .doOnComplete(() -> {
                    assertTrue(storage.getEegEvents().containsAll(sentEvents));
                })
                .subscribe(async::complete, context::fail);
    }
}
