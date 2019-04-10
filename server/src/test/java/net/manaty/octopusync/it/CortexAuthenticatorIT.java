package net.manaty.octopusync.it;

import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import net.manaty.octopusync.it.fixture.CortexTestBase;
import net.manaty.octopusync.it.fixture.emotiv.TestCortexCredentials;
import net.manaty.octopusync.it.fixture.emotiv.TestCortexResources;
import net.manaty.octopusync.service.emotiv.CortexAuthenticator;
import net.manaty.octopusync.service.emotiv.EmotivCredentials;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(VertxUnitRunner.class)
public class CortexAuthenticatorIT extends CortexTestBase {

    private CortexAuthenticator authenticator;

    @Before
    public void setUp() {
        super.setUp();

        TestCortexCredentials credentials = TestCortexResources.loadCredentials().get(1);
        EmotivCredentials emotivCredentials = new EmotivCredentials(
                credentials.getUsername(),
                credentials.getPassword(),
                credentials.getClientId(),
                credentials.getClientSecret(),
                null);

        this.authenticator = new CortexAuthenticator(vertx, client, emotivCredentials, 0);
    }

    @After
    public void tearDown() {
        CortexAuthenticator authenticator = this.authenticator;
        if (authenticator != null) {
            authenticator.stop().blockingAwait();
        }
    }

    @Test
    public void test(TestContext context) {
        Async async = context.async();

        authenticator
                .start()
                .andThen(authenticator.getAuthzToken())
                .doOnSuccess(it -> async.complete())
                .doOnError(context::fail)
                .subscribe();
    }

    @Test
    public void test_WithLogout(TestContext context) {
        Async async = context.async();

        TestCortexCredentials credentials = TestCortexResources.loadCredentials().get(0);
        client.login(credentials.getUsername(), credentials.getPassword(),
                credentials.getClientId(), credentials.getClientSecret())
                .ignoreElement()
                .andThen(authenticator.start())
                .andThen(authenticator.getAuthzToken())
                .doOnSuccess(it -> async.complete())
                .doOnError(context::fail)
                .subscribe();
    }
}
