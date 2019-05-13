package net.manaty.octopusync;

import com.google.inject.Module;
import io.bootique.BQModuleProvider;
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
                new JettyModuleProvider(),
                new JettyWebSocketModuleProvider());
    }
}
