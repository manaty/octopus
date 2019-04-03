package net.manaty.octopusync.it.fixture;

import io.bootique.test.junit.BQDaemonTestFactory;
import io.bootique.test.junit.BQTestFactory;
import io.vertx.ext.unit.junit.RunTestOnContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import io.vertx.reactivex.core.Vertx;
import net.manaty.octopusync.it.fixture.emotiv.TestCortexServerModule;
import net.manaty.octopusync.service.emotiv.CortexClient;
import net.manaty.octopusync.service.emotiv.CortexClientImpl;
import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.rules.RuleChain;
import org.junit.runner.RunWith;

import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.Collections;
import java.util.UUID;

public class CortexTestBase {

    public static BQTestFactory testFactory = new BQTestFactory().autoLoadModules();
    public static BQDaemonTestFactory daemonTestFactory = new BQDaemonTestFactory().autoLoadModules();

    private static TestRuntimeFactory BQ_FACTORY = new TestRuntimeFactory(testFactory, daemonTestFactory);

    public static TestCortexServer cortexServer = new TestCortexServer(
            BQ_FACTORY.buildDaemonRuntimeFactory(
                    Arrays.asList("--server", "--config=classpath:cortex-test.yml"),
                    Collections.emptyMap(),
                    Collections.singleton(TestCortexServerModule.class)));

    @ClassRule
    public static RuleChain stack = RuleChain
            .outerRule(testFactory)
            .around(daemonTestFactory)
            .around(cortexServer);

    @Rule
    public RunTestOnContext vertxRule = new RunTestOnContext();

    protected Vertx vertx;
    protected CortexClient client;

    @Before
    public void setUp() {
        vertx = new Vertx(vertxRule.vertx());
        client = new CortexClientImpl(vertx, vertx.createHttpClient(),
                new InetSocketAddress("localhost", 8080), false);
    }

    @After
    public void tearDown() {
        Vertx vertx = this.vertx;
        if (vertx != null) {
            vertx.close();
        }
    }

    protected static String randomString() {
        return UUID.randomUUID().toString();
    }
}
