package net.manaty.octopusync.it;

import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import net.manaty.octopusync.it.fixture.CortexTestBase;
import net.manaty.octopusync.service.emotiv.CortexAuthenticator;
import net.manaty.octopusync.service.emotiv.EmotivCredentials;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(VertxUnitRunner.class)
public class CortexAuthenticatorIT extends CortexTestBase {

    private CortexAuthenticator authenticator;

    @Before
    public void setUp() {
        super.setUp();
        String username = randomString();
        String clientId = randomString();
        EmotivCredentials credentials = new EmotivCredentials(
                username, "password", clientId, "clientSecret", null);
        this.authenticator = new CortexAuthenticator(vertx, client, credentials, 0);
    }

    @Test
    public void test(TestContext context) {
        Async async = context.async();

        authenticator
                .onNewAuthzTokenIssued(it -> async.complete())
                .start()
                .subscribe(() -> {}, context::fail);
    }
}
