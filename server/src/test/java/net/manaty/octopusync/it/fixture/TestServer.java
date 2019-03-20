package net.manaty.octopusync.it.fixture;

import io.bootique.BQRuntime;
import io.bootique.test.junit.BQDaemonTestFactory;
import io.grpc.ManagedChannel;
import net.manaty.octopusync.api.OctopuSyncGrpc.OctopuSyncBlockingStub;
import net.manaty.octopusync.service.grpc.ManagedChannelFactory;
import org.junit.rules.ExternalResource;

import java.util.function.Supplier;

import static net.manaty.octopusync.service.common.LazySupplier.lazySupplier;

public class TestServer extends ExternalResource {
    // hard-coded here and in server-test.yml
    private static final int SERVER_PORT = 9999;

    private final ManagedChannelFactory channelFactory;

    private final BQDaemonTestFactory testFactory;
    private volatile BQRuntime serverRuntime;
    private volatile Supplier<OctopuSyncBlockingStub> blockingStubSupplier;

    public TestServer(BQDaemonTestFactory testFactory) {
        this.testFactory = testFactory;
        channelFactory = new ManagedChannelFactory();
    }

    @Override
    protected void before() {
        this.serverRuntime = testFactory.app("--server", "--config=classpath:server-test.yml").start();

        ManagedChannel serverChannel = channelFactory.createLocalPlaintextChannel(SERVER_PORT);
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
