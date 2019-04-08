package net.manaty.octopusync.it;

import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import net.manaty.octopusync.it.fixture.CortexTestBase;
import net.manaty.octopusync.it.fixture.emotiv.TestCortexCredentials;
import net.manaty.octopusync.it.fixture.emotiv.TestCortexResources;
import net.manaty.octopusync.service.emotiv.message.Response;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(VertxUnitRunner.class)
public class CortexClientIT extends CortexTestBase {

    private TestCortexCredentials credentials;

    @Before
    public void setUp() {
        super.setUp();

        credentials = TestCortexResources.loadCredentials().get(0);
    }

    @Test
    public void testConnect(TestContext context) {
        Async async = context.async();
        client.connect()
                .subscribe(async::complete, context::fail);
    }

    @Test
    public void testGetUserLogin(TestContext context) {
        Async async = context.async();
        client.connect()
                .andThen(client.getUserLogin())
                .doOnSuccess(this::checkResponseSuccess)
                .subscribe(it -> async.complete(), context::fail);
    }

    @Test
    public void testLogin(TestContext context) {
        Async async = context.async();
        client.connect()
                .andThen(client.login(credentials.getUsername(), credentials.getPassword(),
                        credentials.getClientId(), credentials.getClientSecret()))
                .doOnSuccess(this::checkResponseSuccess)
                .subscribe(it -> async.complete(), context::fail);

    }

    @Test
    public void testAuthorize(TestContext context) {
        Async async = context.async();
        client.connect()
                .andThen(client.login(credentials.getUsername(), credentials.getPassword(),
                        credentials.getClientId(), credentials.getClientSecret()))
                .doOnSuccess(this::checkResponseSuccess)
                .flatMap(it -> client.authorize(credentials.getClientId(), credentials.getClientSecret(), null, 1))
                .doOnSuccess(this::checkResponseSuccess)
                .subscribe(it -> async.complete(), context::fail);

    }

    private void checkResponseSuccess(Response<?> response) {
        if (response.error() != null) {
            Assert.fail("Response error: " + response.error());
        }
    }
}
