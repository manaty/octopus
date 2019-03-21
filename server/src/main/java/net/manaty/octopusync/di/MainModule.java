package net.manaty.octopusync.di;

import com.google.inject.AbstractModule;
import com.google.inject.Injector;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import io.bootique.BQCoreModule;
import io.bootique.config.ConfigurationFactory;
import io.bootique.shutdown.ShutdownManager;
import io.reactivex.Completable;
import io.vertx.reactivex.core.Vertx;
import net.manaty.octopusync.command.ServerCommand;
import net.manaty.octopusync.service.ServerVerticle;
import net.manaty.octopusync.service.grpc.ManagedChannelFactory;
import net.manaty.octopusync.service.s2s.NodeListFactory;
import net.manaty.octopusync.service.s2s.S2STimeSynchronizer;

@SuppressWarnings("unused")
public class MainModule extends AbstractModule {

    @Override
    protected void configure() {
        BQCoreModule.extend(binder()).addCommand(ServerCommand.class);
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
    public NodeListFactory provideNodeListFactory(ConfigurationFactory configurationFactory, Injector injector) {
        return buildGrpcConfiguration(configurationFactory)
                .createNodeListFactory(injector);
    }

    @Provides
    @Singleton
    public ManagedChannelFactory provideManagedChannelFactory(ShutdownManager shutdownManager) {
        ManagedChannelFactory channelFactory = new ManagedChannelFactory();
        shutdownManager.addShutdownHook(channelFactory);
        return channelFactory;
    }

    @Provides
    @Singleton
    public S2STimeSynchronizer provideS2STimeSynchronizer(
            Vertx vertx,
            NodeListFactory nodeListFactory,
            ManagedChannelFactory channelFactory) {

        return new S2STimeSynchronizer(vertx, nodeListFactory, channelFactory);
    }

    @Provides
    @Singleton
    public ServerVerticle provideServerVerticle(
            ConfigurationFactory configurationFactory,
            S2STimeSynchronizer synchronizer) {

        int grpcPort = buildGrpcConfiguration(configurationFactory)
                .getPort();
        return new ServerVerticle(grpcPort, synchronizer);
    }

    private GrpcConfiguration buildGrpcConfiguration(ConfigurationFactory configurationFactory) {
        return configurationFactory.config(GrpcConfiguration.class, "grpc");
    }
}
