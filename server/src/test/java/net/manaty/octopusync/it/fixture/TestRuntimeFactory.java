package net.manaty.octopusync.it.fixture;

import com.google.inject.Module;
import io.bootique.BQRuntime;
import io.bootique.command.CommandOutcome;
import io.bootique.test.junit.BQDaemonTestFactory;
import io.bootique.test.junit.BQTestFactory;
import net.manaty.octopusync.service.common.LazySupplier;
import net.manaty.octopusync.service.common.NetworkUtils;
import org.junit.rules.ExternalResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

import static net.manaty.octopusync.service.common.LazySupplier.lazySupplier;

public class TestRuntimeFactory extends ExternalResource {
    private static final Logger LOGGER = LoggerFactory.getLogger(TestRuntimeFactory.class);

    private final BQTestFactory testFactory;
    private final BQDaemonTestFactory daemonTestFactory;
    private final Set<BQRuntime> runtimes;

    private boolean stopped;

    public TestRuntimeFactory(BQTestFactory testFactory, BQDaemonTestFactory daemonTestFactory) {
        this.testFactory = testFactory;
        this.daemonTestFactory = daemonTestFactory;
        this.runtimes = ConcurrentHashMap.newKeySet();
    }

    public static String buildJdbcUrl(TestPostgresDb db, String database) {
        return buildJdbcUrl(db, database, null);
    }

    public static String buildJdbcUrl(TestPostgresDb db, String database, @Nullable String currentSchema) {
        String url = String.format("jdbc:postgresql://%s:%d/%s", db.host(), db.port(), database);
        if (currentSchema != null) {
            url += ("?currentSchema=" + currentSchema);
        }
        return url;
    }

    public Map<String, String> buildDbProperties(TestPostgresDb db) {
        Map<String, String> properties = new HashMap<>();
        properties.put("bq.jdbc.octopus.url", buildJdbcUrl(db, "octopus", "octopus"));
        properties.put("bq.jdbc.octopus.username", String.valueOf(db.username()));
        properties.put("bq.jdbc.octopus.password", String.valueOf(db.password()));
        properties.put("bq.jdbc.octopus.maxActive", "10");
        return properties;
    }

    public Map<String, String> buildServerProperties(TestPostgresDb db) {
        Map<String, String> properties = new HashMap<>(buildDbProperties(db));
        properties.put("bq.grpc.port", String.valueOf(NetworkUtils.freePort()));
        return properties;
    }

    public ManagedBQRuntime buildRuntimeFactory(List<String> args, Map<String, String> extraBqProperties) {

        LazySupplier<BQRuntime> runtimeFactory = lazySupplier(() -> {
            BQTestFactory.Builder testFactoryBuilder = testFactory.app(args.toArray(new String[0]));
            extraBqProperties.forEach(testFactoryBuilder::property);
            LOGGER.info(buildStartupLogMessage(args, extraBqProperties));
            return testFactoryBuilder.createRuntime();
        });

        return new ManagedBQRuntime() {
            @Override
            public CommandOutcome run() {
                return getRuntime().run();
            }

            @Override
            public BQRuntime getRuntime() {
                return runtimeFactory.get();
            }
        };
    }

    public ManagedBQDaemonRuntime buildDaemonRuntimeFactory(
            List<String> args,
            Map<String, String> extraBqProperties,
            Collection<? extends Class<? extends Module>> extraBqModules) {

        Supplier<BQRuntime> runtimeFactory = () -> {
            BQDaemonTestFactory.Builder testFactoryBuilder = daemonTestFactory.app(args.toArray(new String[0]));
            extraBqProperties.forEach(testFactoryBuilder::property);
            extraBqModules.forEach(testFactoryBuilder::module);
            LOGGER.info(buildStartupLogMessage(args, extraBqProperties));
            return testFactoryBuilder.startupAndWaitCheck().createRuntime();
        };

        return new ManagedBQDaemonRuntime() {
            boolean started;
            volatile BQRuntime runtime;

            @Override
            public synchronized void start() {
                if (!started) {
                    BQRuntime runtime = getOrCreateRuntime();
                    daemonTestFactory.start(runtime);
                    registerRuntime(runtime);
                    started = true;
                }
            }

            @Override
            public synchronized void stop() {
                if (started) {
                    daemonTestFactory.stop(runtime);
                    unregisterRuntime(runtime);
                    started = false;
                    runtime = null;
                }
            }

            @Override
            public BQRuntime getOrCreateRuntime() {
                if (runtime == null) {
                    synchronized (this) {
                        if (runtime == null) {
                            runtime = runtimeFactory.get();
                        }
                    }
                }
                return runtime;
            }
        };
    }

    private static String buildStartupLogMessage(List<String> args, Map<String, String> extraBqProperties) {
        StringBuilder buf = new StringBuilder();
        buf.append("\nRunning BQ with command line parameters:\n");
        args.forEach(arg -> buf.append(String.format("\t`%s`\n", arg)));
        buf.append("JVM options:\n");

        new TreeMap<>(extraBqProperties).forEach((k, v) -> {
            buf.append(String.format("\t`%s`=`%s`\n", k, v));
        });
        return buf.toString();
    }

    private synchronized void registerRuntime(BQRuntime runtime) {
        if (!stopped) {
            if (!runtimes.add(runtime)) {
                throw new IllegalStateException("Tried to register the same runtime multiple times...");
            }
        } else {
            throw new IllegalStateException("Already stopped");
        }
    }

    private synchronized void unregisterRuntime(BQRuntime runtime) {
        if (!stopped) {
            if (!runtimes.remove(runtime)) {
                throw new IllegalStateException("Tried to unregister unknown runtime...");
            }
        } else {
            throw new IllegalStateException("Already stopped");
        }
    }

    @Override
    protected void after() {
        stop();
    }

    public synchronized void stop() {
        if (!stopped) {
            runtimes.forEach(runtime -> {
                try {
                    daemonTestFactory.stop(runtime);
                } catch (Exception e) {
                    LOGGER.error("Failed to stop one of BQ runtimes", e);
                }
            });
            runtimes.clear();
            stopped = true;
        }
    }
}
