package net.manaty.octopusync.di;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import io.bootique.BQCoreModule;
import io.bootique.command.CommandDecorator;
import io.bootique.jetty.command.ServerCommand;
import io.bootique.jetty.websocket.JettyWebSocketModule;
import io.bootique.shutdown.ShutdownManager;
import net.manaty.octopusync.command.OctopusServerCommand;
import net.manaty.octopusync.service.web.WebEventListener;
import net.manaty.octopusync.service.web.admin.AdminEndpoint;

import java.time.Duration;

@SuppressWarnings("unused")
public class WebModule extends AbstractModule {

    @Override
    protected void configure() {
        BQCoreModule.extend(binder()).decorateCommand(
                OctopusServerCommand.class, CommandDecorator.alsoRun(ServerCommand.class));

        JettyWebSocketModule.extend(binder()).addEndpoint(AdminEndpoint.class);

        MainModule.extend(binder()).addEventListenerType(WebEventListener.class);
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
    public WebEventListener provideWebEventListener(AdminEndpoint adminEndpoint) {
        return new WebEventListener(adminEndpoint);
    }
}
