package net.manaty.octopusync.di;

import com.google.inject.AbstractModule;
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
    public ServerVerticle provideServerVerticle(ConfigurationFactory configurationFactory) {
        int grpcPort = configurationFactory.config(GrpcConfiguration.class, "grpc")
                .getPort();
        return new ServerVerticle(grpcPort);
    }

    public ManagedChannelFactory provideManagedChannelFactory(ShutdownManager shutdownManager) {
        ManagedChannelFactory channelFactory = new ManagedChannelFactory();
        shutdownManager.addShutdownHook(channelFactory);
        return channelFactory;
    }
}
