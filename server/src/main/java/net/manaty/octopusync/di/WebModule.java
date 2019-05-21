package net.manaty.octopusync.di;

import com.google.inject.Binder;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import io.bootique.BQCoreModule;
import io.bootique.ConfigModule;
import io.bootique.command.CommandDecorator;
import io.bootique.jersey.JerseyModule;
import io.bootique.jetty.JettyModule;
import io.bootique.jetty.command.ServerCommand;
import io.bootique.jetty.websocket.JettyWebSocketModule;
import io.bootique.shutdown.ShutdownManager;
import net.manaty.octopusync.command.OctopusServerCommand;
import net.manaty.octopusync.service.EventListener;
import net.manaty.octopusync.service.db.Storage;
import net.manaty.octopusync.service.grpc.OctopuSyncGrpcService;
import net.manaty.octopusync.service.web.WebEventListener;
import net.manaty.octopusync.service.web.ws.AdminEndpoint;
import net.manaty.octopusync.service.web.rest.AdminResource;
import net.manaty.octopusync.service.web.rest.ReportResource;

import java.time.Duration;
import java.util.Set;

@SuppressWarnings("unused")
public class WebModule extends ConfigModule {

    @Override
    public void configure(Binder binder) {
        BQCoreModule.extend(binder).decorateCommand(
                OctopusServerCommand.class, CommandDecorator.alsoRun(ServerCommand.class));

        JettyModule.extend(binder).useDefaultServlet();
        JettyWebSocketModule.extend(binder).addEndpoint(AdminEndpoint.class);
        JerseyModule.extend(binder)
                .addResource(ReportResource.class)
                .addResource(AdminResource.class);

        MainModule.extend(binder).addEventListenerType(WebEventListener.class);
    }

    @Provides
    @Singleton
    public AdminEndpoint provideAdminEndpoint(ShutdownManager shutdownManager) {
        // TODO: configurable reporting interval
        AdminEndpoint adminEndpoint = new AdminEndpoint(Duration.ofSeconds(1));
        adminEndpoint.init();
        shutdownManager.addShutdownHook(adminEndpoint::shutdown);
        return adminEndpoint;
    }

    @Provides
    @Singleton
    public WebEventListener provideWebEventListener(AdminEndpoint adminEndpoint, Storage storage) {
        return new WebEventListener(adminEndpoint, storage);
    }

    @Provides
    @Singleton
    // make sure there is one instance of admin resource
    public AdminResource provideAdminResource(
            OctopuSyncGrpcService grpcService,
            Set<EventListener> eventListeners) {

        return new AdminResource(grpcService, eventListeners);
    }
}
