package net.manaty.octopusync.di;

import com.google.inject.*;
import io.bootique.BQCoreModule;
import io.bootique.config.ConfigurationFactory;
import io.bootique.jdbc.DataSourceFactory;
import io.bootique.shutdown.ShutdownManager;
import io.reactivex.Completable;
import io.vertx.core.http.HttpClientOptions;
import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.core.http.HttpClient;
import net.manaty.octopusync.command.OctopusServerCommand;
import net.manaty.octopusync.service.EventListener;
import net.manaty.octopusync.service.ServerVerticle;
import net.manaty.octopusync.service.db.JdbcStorage;
import net.manaty.octopusync.service.db.Storage;
import net.manaty.octopusync.service.emotiv.CortexClient;
import net.manaty.octopusync.service.emotiv.CortexClientImpl;
import net.manaty.octopusync.service.emotiv.CortexService;
import net.manaty.octopusync.service.emotiv.CortexServiceImpl;
import net.manaty.octopusync.service.grpc.ManagedChannelFactory;
import net.manaty.octopusync.service.s2s.NodeListFactory;
import net.manaty.octopusync.service.s2s.S2STimeSynchronizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.Set;

@SuppressWarnings("unused")
public class MainModule extends AbstractModule {
    private static final Logger LOGGER = LoggerFactory.getLogger(MainModule.class);

    public static MainModuleExtender extend(Binder binder) {
        return new MainModuleExtender(binder);
    }

    @Override
    protected void configure() {
        MainModule.extend(binder()).initAllExtensions();
        BQCoreModule.extend(binder()).addCommand(OctopusServerCommand.class);
    }

    @Provides
    @ServerAddress
    @Singleton
    public InetAddress provideServerAddress(ConfigurationFactory configurationFactory) {
        ServerConfiguration serverConfiguration = buildServerConfiguration(configurationFactory);
        return serverConfiguration.resolveAddress();
    }

    @Provides
    @Singleton
    public Vertx provideVertx(ShutdownManager shutdownManager) {
        Vertx vertx = Vertx.vertx();
        // TODO: not a good place for this; move to VertxFactory
        shutdownManager.addShutdownHook(() -> Completable.fromAction(() -> {
            try {
                vertx.deploymentIDs().forEach(deploymentId -> {
                    vertx.rxUndeploy(deploymentId).blockingAwait();
                });
                vertx.close();
            } catch (Exception e) {
                LOGGER.error("Failed to shutdown vertx", e);
            }
        }).blockingAwait());
        return vertx;
    }

    @Provides
    @Singleton
    public NodeListFactory provideNodeListFactory(
            ConfigurationFactory configurationFactory,
            Injector injector,
            @ServerAddress InetAddress serverAddress) {

        NodeListFactory nodeListFactory = buildGrpcConfiguration(configurationFactory)
                .createNodeListFactory(injector);

        return () -> nodeListFactory.map(address -> {
            if (address.getAddress().isLoopbackAddress()) {
                return new InetSocketAddress(serverAddress, address.getPort());
            } else {
                return address;
            }
        });
    }

    @Provides
    @Singleton
    public ManagedChannelFactory provideManagedChannelFactory(ShutdownManager shutdownManager) {
        ManagedChannelFactory channelFactory = new ManagedChannelFactory();
        shutdownManager.addShutdownHook(channelFactory);
        return channelFactory;
    }

    @Provides
    @GrpcPort
    @Singleton
    public int provideGrpcPort(ConfigurationFactory configurationFactory) {
        return buildGrpcConfiguration(configurationFactory).getPort();
    }

    @Provides
    @Singleton
    public S2STimeSynchronizer provideS2STimeSynchronizer(
            ConfigurationFactory configurationFactory,
            Vertx vertx,
            NodeListFactory nodeListFactory,
            ManagedChannelFactory channelFactory,
            @ServerAddress InetAddress serverAddress) {

        GrpcConfiguration grpcConfiguration = buildGrpcConfiguration(configurationFactory);
        InetSocketAddress localGrpcAddress = new InetSocketAddress(serverAddress, grpcConfiguration.getPort());

        return new S2STimeSynchronizer(vertx, nodeListFactory, channelFactory,
                grpcConfiguration.getNodeLookupInterval(), grpcConfiguration.getNodeSyncInterval(),
                localGrpcAddress);
    }

    @Provides
    @Singleton
    public CortexService provideCortexService(
            Vertx vertx,
            CortexClient cortexClient,
            ConfigurationFactory configurationFactory) {

        CortexConfiguration cortexConfiguration = buildCortexConfiguration(configurationFactory);
        return new CortexServiceImpl(vertx, cortexClient, cortexConfiguration.getEmotivCredentials(),
                cortexConfiguration.getHeadsetIdsToCodes().keySet());
    }

    @Provides
    @Singleton
    public ServerVerticle provideServerVerticle(
            @GrpcPort int grpcPort,
            Storage storage,
            S2STimeSynchronizer synchronizer,
            CortexService cortexService,
            Set<EventListener> eventListeners,
            ConfigurationFactory configurationFactory) {

        CortexConfiguration cortexConfiguration = buildCortexConfiguration(configurationFactory);
        return new ServerVerticle(grpcPort, storage, synchronizer,
                cortexService, eventListeners, cortexConfiguration.getHeadsetIdsToCodes());
    }

    private ServerConfiguration buildServerConfiguration(ConfigurationFactory configurationFactory) {
        return configurationFactory.config(ServerConfiguration.class, "server");
    }

    private GrpcConfiguration buildGrpcConfiguration(ConfigurationFactory configurationFactory) {
        return configurationFactory.config(GrpcConfiguration.class, "grpc");
    }

    private CortexConfiguration buildCortexConfiguration(ConfigurationFactory configurationFactory) {
        return configurationFactory.config(CortexConfiguration.class, "cortex");
    }

    @Provides
    @Singleton
    public Storage provideStorage(Vertx vertx, DataSourceFactory dataSourceFactory) {
        return new JdbcStorage(vertx, () -> dataSourceFactory.forName("octopus"));
    }

    @Provides
    @Singleton
    public HttpClient provideHttpClient(Vertx vertx, ShutdownManager shutdownManager) {
        return vertx.createHttpClient(new HttpClientOptions()
                // TODO: quick fix for not having Cortex cert in JKS
                .setTrustAll(true)
                .setKeepAlive(true)
                .setTcpKeepAlive(true));
    }

    @Provides
    @Singleton
    public CortexClient provideCortexClient(Vertx vertx, HttpClient httpClient, ConfigurationFactory configurationFactory) {
        CortexConfiguration cortexConfiguration = buildCortexConfiguration(configurationFactory);
        return new CortexClientImpl(vertx, httpClient,
                cortexConfiguration.resolveCortexServerAddress(), cortexConfiguration.shouldUseSsl());
    }
}
