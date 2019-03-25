package net.manaty.octopusync.it.fixture;

import com.google.inject.Key;
import io.bootique.BQRuntime;
import io.bootique.test.junit.BQDaemonTestFactory;
import io.grpc.ManagedChannel;
import net.manaty.octopusync.api.OctopuSyncGrpc.OctopuSyncBlockingStub;
import net.manaty.octopusync.di.GrpcPort;
import net.manaty.octopusync.service.grpc.ManagedChannelFactory;
import org.junit.rules.ExternalResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.function.Supplier;

import static net.manaty.octopusync.service.common.LazySupplier.lazySupplier;

public class TestServer extends ExternalResource {
    private static final Logger LOGGER = LoggerFactory.getLogger(TestServer.class);

    private final ManagedChannelFactory channelFactory;

    private final BQDaemonTestFactory testFactory;
    private final String pathToBqConfig;
    private final Map<String, String> extraBqProperties;

    private volatile BQRuntime serverRuntime;
    private volatile Supplier<OctopuSyncBlockingStub> blockingStubSupplier;

    public TestServer(BQDaemonTestFactory testFactory, String pathToBqConfig, Map<String, String> extraBqProperties) {
        this.testFactory = testFactory;
        this.pathToBqConfig = pathToBqConfig;
        this.extraBqProperties = extraBqProperties;
        this.channelFactory = new ManagedChannelFactory();
    }

    @Override
    protected void before() {
        List<String> args = Arrays.asList("--server", "--config=" + pathToBqConfig);

        StringBuilder buf = new StringBuilder();
        buf.append("\nRunning BQ with command line parameters:\n");
        args.forEach(arg -> buf.append(String.format("\t`%s`\n", arg)));
        buf.append("JVM options:\n");

        BQDaemonTestFactory.Builder testFactoryBuilder = testFactory.app(args.toArray(new String[0]));
        new TreeMap<>(extraBqProperties).forEach((k, v) -> {
            buf.append(String.format("\t`%s`=`%s`\n", k, v));
            testFactoryBuilder.property(k, v);
        });

        this.serverRuntime = testFactoryBuilder.start();
        LOGGER.info(buf.toString());

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
