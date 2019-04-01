package net.manaty.octopusync.it.fixture;

import io.bootique.test.junit.BQDaemonTestFactory;
import io.bootique.test.junit.BQTestFactory;
import net.manaty.octopusync.it.fixture.emotiv.TestCortexServerModule;
import org.junit.ClassRule;
import org.junit.rules.RuleChain;
import org.junit.rules.TestRule;

import java.util.Arrays;
import java.util.Collections;

public abstract class ServerTestBase {

    public static BQTestFactory testFactory = new BQTestFactory().autoLoadModules();
    public static BQDaemonTestFactory daemonTestFactory = new BQDaemonTestFactory().autoLoadModules();

    private static TestRuntimeFactory BQ_FACTORY = new TestRuntimeFactory(testFactory, daemonTestFactory);

    public static TestCortexServer cortexServer = new TestCortexServer(
            BQ_FACTORY.buildDaemonRuntimeFactory(
                    Arrays.asList("--server", "--config=classpath:cortex-test.yml"),
                    Collections.emptyMap(),
                    Collections.singleton(TestCortexServerModule.class)));

    public static TestPostgresDb db = new TestPostgresDb(
            TestPostgresDb.testConfigWithRandomPort(),
            TestConstants.EMBEDDED_POSTGRES_CACHE_PATH);

    public static TestLiquibaseDb liquibaseDb = new TestLiquibaseDb(
            db,
            BQ_FACTORY.buildRuntimeFactory(
                    Arrays.asList("--lb-update", "--lb-default-schema=octopus", "--config=classpath:db-test.yml"),
                    BQ_FACTORY.buildDbProperties(db)),
            BQ_FACTORY.buildRuntimeFactory(
                    Arrays.asList("--lb-drop-all", "--lb-default-schema=octopus", "--config=classpath:db-test.yml"),
                    BQ_FACTORY.buildDbProperties(db)));

    public static TestServer server = new TestServer(
            BQ_FACTORY.buildDaemonRuntimeFactory(
                    Arrays.asList("--octopus-server", "--config=classpath:server-test.yml"),
                    BQ_FACTORY.buildServerProperties(db),
                    Collections.emptySet()));

    @ClassRule
    public static TestRule testStackRule = RuleChain
            .outerRule(testFactory)
            .around(daemonTestFactory)
            .around(cortexServer)
            .around(db)
            .around(liquibaseDb)
            .around(server);
}
