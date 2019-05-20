package net.manaty.octopusync;

import com.google.inject.Module;
import io.bootique.BQModule;
import io.bootique.BQModuleProvider;
import io.bootique.jdbc.JdbcModuleProvider;
import io.bootique.jdbc.tomcat.JdbcTomcatModuleProvider;
import io.bootique.logback.LogbackModuleProvider;
import net.manaty.octopusync.di.CortexConfiguration;
import net.manaty.octopusync.di.GrpcConfiguration;
import net.manaty.octopusync.di.MainModule;
import net.manaty.octopusync.di.ServerConfiguration;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class MainModuleProvider implements BQModuleProvider {

    @Override
    public Module module() {
        return new MainModule();
    }

    @Override
    public Collection<BQModuleProvider> dependencies() {
        return Arrays.asList(
                new JdbcModuleProvider(),
                new JdbcTomcatModuleProvider(),
                new LogbackModuleProvider());
    }

    @Override
    public Map<String, Type> configs() {
        // TODO: config prefix is hardcoded. Refactor away from ConfigModule, and make provider
        // generate config prefix, reusing it in metadata...
        Map<String, Type> m = new HashMap<>();
        m.put("server", ServerConfiguration.class);
        m.put("cortex", CortexConfiguration.class);
        m.put("grpc", GrpcConfiguration.class);
        return m;
    }

    @Override
    public BQModule.Builder moduleBuilder() {
        return BQModuleProvider.super
                .moduleBuilder()
                .description("Provides main OctopuSync facilities" +
                        " (gRPC API for client and server-to-server communication," +
                        " integration with Emotiv, storage, etc.)");
    }
}
