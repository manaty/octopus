package net.manaty.octopusync.command;

import io.bootique.cli.Cli;
import io.bootique.command.CommandOutcome;
import io.bootique.command.CommandWithMetadata;
import io.bootique.meta.application.CommandMetadata;
import io.bootique.shutdown.ShutdownManager;
import io.vertx.reactivex.core.RxHelper;
import io.vertx.reactivex.core.Vertx;
import net.manaty.octopusync.service.ServerVerticle;

import javax.inject.Inject;
import javax.inject.Provider;

public class OctopusServerCommand extends CommandWithMetadata {

    private final Provider<Vertx> vertx;
    private final Provider<ServerVerticle> serverVerticle;
    private final Provider<ShutdownManager> shutdownManager;

    @Inject
    public OctopusServerCommand(
            Provider<Vertx> vertx,
            Provider<ServerVerticle> serverVerticle,
            Provider<ShutdownManager> shutdownManager) {

        super(CommandMetadata.builder(OctopusServerCommand.class));

        this.vertx = vertx;
        this.serverVerticle = serverVerticle;
        this.shutdownManager = shutdownManager;
    }

    @Override
    public CommandOutcome run(Cli cli) {
        Vertx vertx;
        ServerVerticle serverVerticle;

        try {
            vertx = this.vertx.get();
            serverVerticle = this.serverVerticle.get();
        } catch (Exception e) {
            return CommandOutcome.failed(1, e);
        }

        return RxHelper.deployVerticle(vertx, serverVerticle)
                .map(deploymentId -> {
//                    shutdownManager.get().addShutdownHook(() -> {
//                        vertx.rxUndeploy(deploymentId).blockingAwait();
//                    });
                    return CommandOutcome.succeededAndForkedToBackground();
                })
                .onErrorReturn(e -> CommandOutcome.failed(1, e))
                .blockingGet();
    }
}
