package net.manaty.octopusync;

import com.google.inject.Module;
import io.bootique.BQModule;
import io.bootique.BQModuleProvider;
import io.bootique.jersey.JerseyModuleProvider;
import io.bootique.jetty.JettyModuleProvider;
import io.bootique.jetty.websocket.JettyWebSocketModuleProvider;
import net.manaty.octopusync.di.WebModule;

import java.util.Arrays;
import java.util.Collection;

public class WebModuleProvider implements BQModuleProvider {

    @Override
    public Module module() {
        return new WebModule();
    }

    @Override
    public Collection<BQModuleProvider> dependencies() {
        return Arrays.asList(
                new MainModuleProvider(),
                new JettyModuleProvider(),
                new JettyWebSocketModuleProvider(),
                new JerseyModuleProvider());
    }

    @Override
    public BQModule.Builder moduleBuilder() {
        return BQModuleProvider.super
                .moduleBuilder()
                .description("Provides OctopuSync Web API for users and administrators.");
    }
}
