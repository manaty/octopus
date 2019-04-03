package net.manaty.octopusync.it.fixture.emotiv;

import com.google.inject.AbstractModule;
import io.bootique.jetty.JettyModule;

public class TestCortexServerModule extends AbstractModule {

    @Override
    protected void configure() {
        JettyModule.extend(binder())
                .addServlet(CortexSocketCreatorServlet.class);

        binder().bind(UserInfoService.class).asEagerSingleton();
    }
}
