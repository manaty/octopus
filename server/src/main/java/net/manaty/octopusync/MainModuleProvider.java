package net.manaty.octopusync;

import com.google.inject.Module;
import io.bootique.BQModuleProvider;
import io.bootique.jdbc.JdbcModuleProvider;
import io.bootique.jdbc.tomcat.JdbcTomcatModuleProvider;
import io.bootique.logback.LogbackModuleProvider;
import net.manaty.octopusync.di.MainModule;

import java.util.Arrays;
import java.util.Collection;

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
}
