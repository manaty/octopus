package net.manaty.octopusync.it.fixture;

import com.google.inject.Key;
import io.bootique.BQRuntime;
import io.bootique.test.junit.BQDaemonTestFactory;
import io.grpc.ManagedChannel;
import net.manaty.octopusync.api.OctopuSyncGrpc.OctopuSyncBlockingStub;
import net.manaty.octopusync.di.GrpcPort;
import net.manaty.octopusync.service.grpc.ManagedChannelFactory;
import org.junit.rules.ExternalResource;

import java.util.function.Supplier;

import static net.manaty.octopusync.service.common.LazySupplier.lazySupplier;

public class TestServer extends ExternalResource {

    private final ManagedChannelFactory channelFactory;

    private final BQDaemonTestFactory testFactory;
    private final String pathToBqConfig;
    private volatile BQRuntime serverRuntime;
    private volatile Supplier<OctopuSyncBlockingStub> blockingStubSupplier;

    public TestServer(BQDaemonTestFactory testFactory, String pathToBqConfig) {
        this.testFactory = testFactory;
        this.pathToBqConfig = pathToBqConfig;
        this.channelFactory = new ManagedChannelFactory();
    }

    @Override
    protected void before() {
        this.serverRuntime = testFactory.app("--server", "--config=" + pathToBqConfig).start();

        int grpcPort = serverRuntime.getInstance(Key.get(int.class, GrpcPort.class));
        ManagedChannel serverChannel = channelFactory.createLocalPlaintextChannel(grpcPort);
        this.blockingStubSupplier = lazySupplier(() -> new OctopuSyncBlockingStub(serverChannel));
    }

    @Override
    protected void after() {
        channelFactory.close();

        BQRuntime serverRuntime = this.serverRuntime;
        if (serverRuntime != null) {
            testFactory.stop(serverRuntime);
        }
    }

    public OctopuSyncBlockingStub blockingGrpcStub() {
        return blockingStubSupplier.get();
    }
}
