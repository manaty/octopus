package net.manaty.octopusync.it.fixture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.nio.file.Paths;

public class TestConstants {
    private static final Logger LOGGER = LoggerFactory.getLogger(TestConstants.class);

    public static final Path EMBEDDED_POSTGRES_CACHE_PATH;

    static {
        String embeddedPostgresCachePathProperty = "embedded_postgres_cache_path";
        String embeddedPostgresCachePath = System.getProperty(embeddedPostgresCachePathProperty);
        if (embeddedPostgresCachePath == null || embeddedPostgresCachePath.isEmpty()) {
            throw new IllegalStateException("Required system property is not defined: " + embeddedPostgresCachePathProperty);
        }
        EMBEDDED_POSTGRES_CACHE_PATH = Paths.get(embeddedPostgresCachePath);
        LOGGER.info("Embedded Postgres cache path: {}", EMBEDDED_POSTGRES_CACHE_PATH);
    }
}
