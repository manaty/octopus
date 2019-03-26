package net.manaty.octopusync.di;

import com.google.inject.*;
import io.bootique.BQCoreModule;
import io.bootique.config.ConfigurationFactory;
import io.bootique.jdbc.DataSourceFactory;
import io.bootique.shutdown.ShutdownManager;
import io.reactivex.Completable;
import io.vertx.reactivex.core.Vertx;
import net.manaty.octopusync.command.ServerCommand;
import net.manaty.octopusync.service.ServerVerticle;
import net.manaty.octopusync.service.db.JdbcStorage;
import net.manaty.octopusync.service.db.Storage;
import net.manaty.octopusync.service.grpc.ManagedChannelFactory;
import net.manaty.octopusync.service.s2s.NodeListFactory;
import net.manaty.octopusync.service.s2s.S2STimeSynchronizer;

import java.net.InetAddress;
import java.net.InetSocketAddress;

@SuppressWarnings("unused")
public class MainModule extends AbstractModule {

    @Override
    protected void configure() {
        BQCoreModule.extend(binder()).addCommand(ServerCommand.class);
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
            vertx.deploymentIDs().forEach(deploymentId -> {
                vertx.rxUndeploy(deploymentId).blockingAwait();
            });
            vertx.close();
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
    public ServerVerticle provideServerVerticle(
            @GrpcPort int grpcPort,
            Storage storage,
            S2STimeSynchronizer synchronizer) {

        return new ServerVerticle(grpcPort, storage, synchronizer);
    }

    private ServerConfiguration buildServerConfiguration(ConfigurationFactory configurationFactory) {
        return configurationFactory.config(ServerConfiguration.class, "server");
    }

    private GrpcConfiguration buildGrpcConfiguration(ConfigurationFactory configurationFactory) {
        return configurationFactory.config(GrpcConfiguration.class, "grpc");
    }

    @Provides
    @Singleton
    public Storage provideStorage(Vertx vertx, DataSourceFactory dataSourceFactory) {
        return new JdbcStorage(vertx, () -> dataSourceFactory.forName("octopus"));
    }
}
