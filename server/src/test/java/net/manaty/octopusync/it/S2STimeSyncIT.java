package net.manaty.octopusync.it;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.RunTestOnContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import io.vertx.reactivex.core.Vertx;
import net.manaty.octopusync.it.fixture.ServerTestBase;
import net.manaty.octopusync.it.fixture.TestServer;
import net.manaty.octopusync.model.S2STimeSyncResult;
import net.manaty.octopusync.service.grpc.ManagedChannelFactory;
import net.manaty.octopusync.service.s2s.S2STimeSynchronizer;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.time.Duration;
import java.util.Iterator;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Supplier;

@RunWith(VertxUnitRunner.class)
public class S2STimeSyncIT extends ServerTestBase {

    @Rule
    public RunTestOnContext vertxRule = new RunTestOnContext();

    private Vertx vertx;
    private ManagedChannelFactory channelFactory;
    private S2STimeSynchronizer synchronizer;

    @Before
    public void setUp() {
        vertx = new Vertx(vertxRule.vertx());
        channelFactory = new ManagedChannelFactory();
        Supplier<InetSocketAddress> masterServerAddressFactory = () -> server.grpcAddress();
        Duration masterLookupInterval = Duration.ofMinutes(60);
        Duration masterSyncInterval = Duration.ofSeconds(1);

        synchronizer = new S2STimeSynchronizer(
                vertx, masterServerAddressFactory, channelFactory,
                masterLookupInterval, masterSyncInterval, server.grpcAddress());
    }

    @After
    public void tearDown() {
        synchronizer.stopSync();
        channelFactory.close();

        Vertx vertx = this.vertx;
        if (vertx != null) {
            vertx.close();
        }
    }

    @Test
    public void test(TestContext context) {
        synchronizer.startSync()
                .subscribe(new S2STimeSyncResultChecker(server, context));
    }
}

class S2STimeSyncResultChecker implements Observer<S2STimeSyncResult> {
    private static final Logger LOGGER = LoggerFactory.getLogger(S2STimeSyncResultChecker.class);

    private static final int ITEMS_TO_CHECK_PER_STAGE = 3;

    enum State {
        CHECKING_SYNC_BEFORE_RESTART,
        CHECKING_SYNC_DURING_RESTART,
        CHECKING_SYNC_AFTER_RESTART,
        ALL_CHECKS_PASSED
    }

    private final TestServer server;
    private final TestContext testContext;
    private final Async testCondition;
    private final LinkedBlockingQueue<S2STimeSyncResult> resultQueue;
    private final AtomicInteger resultQueueCheckedItemsCount;

    private volatile State state;
    private volatile Disposable subscription;

    public S2STimeSyncResultChecker(TestServer server, TestContext context) {
        this.server = server;
        this.testContext = context;
        this.testCondition = context.async();
        this.resultQueue = new LinkedBlockingQueue<>();
        this.resultQueueCheckedItemsCount = new AtomicInteger(0);
        this.state = State.CHECKING_SYNC_BEFORE_RESTART;
    }

    @Override
    public void onSubscribe(Disposable d) {
        subscription = d;
    }

    @Override
    public void onNext(S2STimeSyncResult syncResult) {
        LOGGER.info("Next sync result observed: " + syncResult);
        try {
            resultQueue.put(syncResult);
        } catch (InterruptedException e) {
            testContext.fail(new IllegalStateException("Unexpectedly interrupted", e));
        }

        switch (state) {
            case CHECKING_SYNC_BEFORE_RESTART:
            case CHECKING_SYNC_AFTER_RESTART: {
                // skip first sync result, because stddev might be big due to initial connection establishing
                int skip = resultQueueCheckedItemsCount.get() + 1;
                if ((resultQueue.size() - skip) >= ITEMS_TO_CHECK_PER_STAGE) {
                    checkResultQueue(skip, item -> {
                        testContext.assertNull(item.getError());
                    });
                    resultQueueCheckedItemsCount.set(resultQueue.size());
                    if (state == State.CHECKING_SYNC_BEFORE_RESTART) {
                        server.stop();
                        LOGGER.info("Stopped the server; awaiting sync failures while it is down...");
                        state = State.CHECKING_SYNC_DURING_RESTART;
                    } else {
                        subscription.dispose();
                        testCondition.complete();
                        state = State.ALL_CHECKS_PASSED;
                    }
                }
                break;
            }
            case CHECKING_SYNC_DURING_RESTART: {
                int skip = resultQueueCheckedItemsCount.get();
                if ((resultQueue.size() - skip) >= ITEMS_TO_CHECK_PER_STAGE) {
                    checkResultQueue(skip, item -> {
                        testContext.assertNotNull(syncResult.getError());
                    });
                    resultQueueCheckedItemsCount.set(resultQueue.size());
                    server.start();
                    LOGGER.info("Started the server; awaiting successful sync's...");
                    state = State.CHECKING_SYNC_AFTER_RESTART;
                }
                break;
            }
            // sanity check
            default: {
                testContext.fail(new IllegalStateException("Unexpected state: " + state));
            }
        }
    }

    private void checkResultQueue(int beginPosition, Consumer<S2STimeSyncResult> itemChecker) {
        Iterator<S2STimeSyncResult> iter = resultQueue.stream()
                .skip(beginPosition)
                .iterator();
        while (iter.hasNext()) {
            itemChecker.accept(iter.next());
        }
    }

    @Override
    public void onError(Throwable e) {
        testContext.fail(new IllegalStateException("Unexpected onError() call in sync result observer", e));
    }

    @Override
    public void onComplete() {
        testContext.fail("Unexpected onComplete() call in sync result observer");
    }
}