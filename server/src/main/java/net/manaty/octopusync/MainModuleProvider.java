package net.manaty.octopusync;

import com.google.inject.Module;
import io.bootique.BQModuleProvider;
import net.manaty.octopusync.di.MainModule;

public class MainModuleProvider implements BQModuleProvider {
    @Override
    public Module module() {
        return new MainModule();
    }
}
