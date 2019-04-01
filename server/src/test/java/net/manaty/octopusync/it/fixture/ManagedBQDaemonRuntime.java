package net.manaty.octopusync.it.fixture;

import io.bootique.BQRuntime;

public interface ManagedBQDaemonRuntime {

    void start();

    void stop();

    BQRuntime getOrCreateRuntime();
}
