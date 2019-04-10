package net.manaty.octopusync.it.fixture;

import org.junit.rules.ExternalResource;

public class TestCortexServer extends ExternalResource {

    private final ManagedBQDaemonRuntime runtime;

    public TestCortexServer(ManagedBQDaemonRuntime runtime) {
        this.runtime = runtime;
    }

    @Override
    protected void before() {
        runtime.start();
    }
}
