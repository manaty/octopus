package net.manaty.octopusync.it.fixture.emotiv;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import io.bootique.jetty.JettyModule;

public class TestCortexServerModule extends AbstractModule {

    @Override
    protected void configure() {
        JettyModule.extend(binder())
                .addServlet(CortexSocketCreatorServlet.class);
    }

    @Provides
    @Singleton
    public CortexInfoService provideUserInfoService() {
        return new CortexInfoService(TestCortexResources.loadCredentials(), TestCortexResources.loadSessions());
    }
}
