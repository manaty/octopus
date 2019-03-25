package net.manaty.octopusync.it.fixture;

import com.google.inject.Key;
import io.bootique.BQRuntime;
import io.bootique.test.junit.BQDaemonTestFactory;
import io.grpc.ManagedChannel;
import net.manaty.octopusync.api.OctopuSyncGrpc.OctopuSyncBlockingStub;
import net.manaty.octopusync.di.GrpcPort;
import net.manaty.octopusync.di.ServerAddress;
import net.manaty.octopusync.service.grpc.ManagedChannelFactory;
import org.junit.rules.ExternalResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

import static net.manaty.octopusync.service.common.LazySupplier.lazySupplier;

public class TestServer extends ExternalResource {
    private static final Logger LOGGER = LoggerFactory.getLogger(TestServer.class);

    private final BQDaemonTestFactory testFactory;
    private final Supplier<BQRuntime> runtimeFactory;
    private final AtomicBoolean started;

    private volatile BQRuntime serverRuntime;
    private volatile ManagedChannelFactory channelFactory;
    private volatile Supplier<OctopuSyncBlockingStub> blockingStubSupplier;

    public TestServer(BQDaemonTestFactory testFactory, String pathToBqConfig, Map<String, String> extraBqProperties) {
        this.testFactory = testFactory;
        this.runtimeFactory = initRuntimeFactory(testFactory, pathToBqConfig, extraBqProperties);
        this.started = new AtomicBoolean(false);
    }

    private static Supplier<BQRuntime> initRuntimeFactory(
            BQDaemonTestFactory testFactory,
            String pathToBqConfig,
            Map<String, String> extraBqProperties) {

        List<String> args = Arrays.asList("--server", "--config=" + pathToBqConfig);

        StringBuilder buf = new StringBuilder();
        buf.append("\nRunning BQ with command line parameters:\n");
        args.forEach(arg -> buf.append(String.format("\t`%s`\n", arg)));
        buf.append("JVM options:\n");

        new TreeMap<>(extraBqProperties).forEach((k, v) -> {
            buf.append(String.format("\t`%s`=`%s`\n", k, v));
        });

        return () -> {
            BQDaemonTestFactory.Builder testFactoryBuilder = testFactory.app(args.toArray(new String[0]));
            extraBqProperties.forEach(testFactoryBuilder::property);
            LOGGER.info(buf.toString());
            return testFactoryBuilder.start();
        };
    }

    @Override
    protected void before() {
        start();
    }

    public void start() {
        if (started.compareAndSet(false, true)) {
            synchronized (this) {
                serverRuntime = runtimeFactory.get();
                channelFactory = new ManagedChannelFactory();
                ManagedChannel serverChannel = channelFactory.createLocalPlaintextChannel(grpcAddress().getPort());
                blockingStubSupplier = lazySupplier(() -> new OctopuSyncBlockingStub(serverChannel));
            }
        } else {
            throw new IllegalStateException("Already started");
        }
    }

    @Override
    protected void after() {
        stop(false);
    }

    public void stop() {
        stop(true);
    }

    private void stop(boolean failIfNotStarted) {
        if (started.compareAndSet(true,false)) {
            synchronized (this) {
                blockingStubSupplier = null;

                try {
                    channelFactory.close();
                } catch (Exception e) {
                    LOGGER.error("Failed to close channel factory", e);
                } finally {
                    channelFactory = null;
                }

                try {
                    testFactory.stop(serverRuntime);
                } finally {
                    serverRuntime = null;
                }
            }
        } else if (failIfNotStarted) {
            throw new IllegalStateException("Not started yet");
        }
    }

    public synchronized OctopuSyncBlockingStub blockingGrpcStub() {
        return blockingStubSupplier.get();
    }

    public synchronized InetSocketAddress grpcAddress() {
        InetAddress address = serverRuntime.getInstance(Key.get(InetAddress.class, ServerAddress.class));
        int grpcPort = serverRuntime.getInstance(Key.get(int.class, GrpcPort.class));
        return new InetSocketAddress(address, grpcPort);
    }
}
