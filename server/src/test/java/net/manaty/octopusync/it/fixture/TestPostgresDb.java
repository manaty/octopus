package net.manaty.octopusync.it.fixture;

import de.flapdoodle.embed.process.config.IRuntimeConfig;
import de.flapdoodle.embed.process.distribution.IVersion;
import net.manaty.octopusync.service.common.NetworkUtils;
import org.junit.rules.ExternalResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.yandex.qatools.embed.postgresql.EmbeddedPostgres;
import ru.yandex.qatools.embed.postgresql.PostgresProcess;
import ru.yandex.qatools.embed.postgresql.PostgresStarter;
import ru.yandex.qatools.embed.postgresql.config.AbstractPostgresConfig.Credentials;
import ru.yandex.qatools.embed.postgresql.config.AbstractPostgresConfig.Net;
import ru.yandex.qatools.embed.postgresql.config.AbstractPostgresConfig.Storage;
import ru.yandex.qatools.embed.postgresql.config.AbstractPostgresConfig.Timeout;
import ru.yandex.qatools.embed.postgresql.config.PostgresConfig;
import ru.yandex.qatools.embed.postgresql.distribution.Version;

import java.nio.file.Path;

public class TestPostgresDb extends ExternalResource {
    private static final Logger LOGGER = LoggerFactory.getLogger(TestPostgresDb.class);

    public static PostgresConfig testConfigWithRandomPort() {
        return testConfig(NetworkUtils.freePort());
    }

    public static PostgresConfig testConfig(int port) {
        try {
            IVersion version = Version.V10_6;
            Net network = new Net("localhost", port);
            Storage storage = new Storage("octopus-test");
            Timeout timeout = new Timeout(30_000);
            Credentials credentials = new Credentials("postgres", "postgres");

            return new PostgresConfig(version, network, storage, timeout, credentials);
        } catch (Exception e) {
            throw new RuntimeException("Failed to create test config", e);
        }
    }

    private final PostgresConfig config;
    private final Path cachedPath;
    private volatile PostgresProcess postgres;

    public TestPostgresDb(PostgresConfig config, Path cachedPath) {
        this.config = config;
        this.cachedPath = cachedPath;
    }

    public String host() {
        return config.net().host();
    }

    public int port() {
        return config.net().port();
    }

    public String username() {
        return config.credentials().username();
    }

    public String password() {
        return config.credentials().password();
    }

    @Override
    protected void before() throws Throwable {
        IRuntimeConfig runtime = EmbeddedPostgres.cachedRuntimeConfig(cachedPath);
        postgres = PostgresStarter.getInstance(runtime)
                .prepare(config)
                .start();
    }

    @Override
    protected void after() {
        PostgresProcess postgres = this.postgres;
        if (postgres != null) {
            try {
                postgres.stop();
            } catch (Exception e) {
                LOGGER.error("Failed to stop embedded postgres (config: " + config + ")", e);
            }
        }
    }
}
