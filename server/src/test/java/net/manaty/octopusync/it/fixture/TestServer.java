package net.manaty.octopusync.it.fixture;

import com.google.inject.Key;
import io.grpc.ManagedChannel;
import net.manaty.octopusync.api.OctopuSyncGrpc.OctopuSyncBlockingStub;
import net.manaty.octopusync.di.GrpcPort;
import net.manaty.octopusync.service.grpc.ManagedChannelFactory;
import org.junit.rules.ExternalResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

import static net.manaty.octopusync.service.common.LazySupplier.lazySupplier;

public class TestServer extends ExternalResource {
    private static final Logger LOGGER = LoggerFactory.getLogger(TestServer.class);

    private final ManagedBQDaemonRuntime runtime;
    private final AtomicBoolean started;

    private volatile ManagedChannelFactory channelFactory;
    private volatile Supplier<OctopuSyncBlockingStub> blockingStubSupplier;

    public TestServer(ManagedBQDaemonRuntime runtime) {
        this.runtime = runtime;
        this.started = new AtomicBoolean(false);
    }

    @Override
    protected void before() {
        start();
    }

    public void start() {
        if (started.compareAndSet(false, true)) {
            synchronized (this) {
                runtime.start();
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

                runtime.stop();
            }
        } else if (failIfNotStarted) {
            throw new IllegalStateException("Not started yet");
        }
    }

    public synchronized OctopuSyncBlockingStub blockingGrpcStub() {
        return blockingStubSupplier.get();
    }

    public synchronized InetSocketAddress grpcAddress() {
        int grpcPort = runtime.getOrCreateRuntime().getInstance(Key.get(int.class, GrpcPort.class));
        return new InetSocketAddress(InetAddress.getLoopbackAddress(), grpcPort);
    }
}
