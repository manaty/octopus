package net.manaty.octopusync.it;

import io.bootique.test.junit.BQDaemonTestFactory;
import net.manaty.octopusync.it.fixture.TestPostgresDb;
import net.manaty.octopusync.it.fixture.TestServer;
import net.manaty.octopusync.service.common.NetworkUtils;
import org.junit.ClassRule;

import java.util.HashMap;
import java.util.Map;

public abstract class ServerTestBase {

    @ClassRule
    public static TestPostgresDb db = new TestPostgresDb(
            TestPostgresDb.testConfigWithRandomPort(),
            TestConstants.EMBEDDED_POSTGRES_CACHE_PATH);

    @ClassRule
    public static BQDaemonTestFactory testFactory = new BQDaemonTestFactory()
            .autoLoadModules();

    @ClassRule
    public static TestServer server = new TestServer(
            testFactory,
            "classpath:server-test.yml",
            buildBqProperties(db));

    private static Map<String, String> buildBqProperties(TestPostgresDb db) {
        Map<String, String> properties = new HashMap<>();
        properties.put("bq.jdbc.octopus.url", String.format("jdbc:postgresql://%s:%d/octopus", db.host(), db.port()));
        properties.put("bq.jdbc.octopus.username", String.valueOf(db.username()));
        properties.put("bq.jdbc.octopus.password", String.valueOf(db.password()));
        properties.put("bq.jdbc.octopus.maxActive", "10");
        // other props
        properties.put("bq.grpc.port", String.valueOf(NetworkUtils.freePort()));
        return properties;
    }
}
