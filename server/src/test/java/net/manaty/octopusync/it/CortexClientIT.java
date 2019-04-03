package net.manaty.octopusync.it;

import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import net.manaty.octopusync.it.fixture.CortexTestBase;
import net.manaty.octopusync.service.emotiv.message.Response;
import org.junit.Assert;
import org.junit.Test;

public class CortexClientIT extends CortexTestBase {

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
        String username = randomString();
        String clientId = randomString();
        client.connect()
                .andThen(client.login(username, "password", clientId, "clientSecret"))
                .doOnSuccess(this::checkResponseSuccess)
                .subscribe(it -> async.complete(), context::fail);

    }

    @Test
    public void testAuthorize(TestContext context) {
        Async async = context.async();
        String username = randomString();
        String clientId = randomString();
        client.connect()
                .andThen(client.login(username, "password", clientId, "clientSecret"))
                .doOnSuccess(this::checkResponseSuccess)
                .flatMap(it -> client.authorize(clientId, "clientSecret", null, 1))
                .doOnSuccess(this::checkResponseSuccess)
                .subscribe(it -> async.complete(), context::fail);

    }

    private void checkResponseSuccess(Response<?> response) {
        if (response.error() != null) {
            Assert.fail("Response error: " + response.error());
        }
    }
}
