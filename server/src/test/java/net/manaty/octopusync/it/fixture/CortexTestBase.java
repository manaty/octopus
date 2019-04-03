package net.manaty.octopusync.it.fixture;

import io.bootique.test.junit.BQDaemonTestFactory;
import io.bootique.test.junit.BQTestFactory;
import net.manaty.octopusync.it.fixture.emotiv.TestCortexServerModule;
import org.junit.ClassRule;
import org.junit.rules.RuleChain;

import java.util.Arrays;
import java.util.Collections;

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
}
