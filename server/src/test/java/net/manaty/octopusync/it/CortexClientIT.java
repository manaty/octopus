package net.manaty.octopusync.it;

import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.RunTestOnContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import io.vertx.reactivex.core.Vertx;
import net.manaty.octopusync.it.fixture.CortexTestBase;
import net.manaty.octopusync.service.emotiv.CortexClient;
import net.manaty.octopusync.service.emotiv.CortexClientImpl;
import net.manaty.octopusync.service.emotiv.message.LoginResponse;
import org.junit.*;
import org.junit.runner.RunWith;

import java.net.InetSocketAddress;

@RunWith(VertxUnitRunner.class)
public class CortexClientIT extends CortexTestBase {

    @Rule
    public RunTestOnContext vertxRule = new RunTestOnContext();

    private Vertx vertx;
    private CortexClient client;

    @Before
    public void setUp() {
        vertx = new Vertx(vertxRule.vertx());
        client = new CortexClientImpl(vertx, vertx.createHttpClient(),
                new InetSocketAddress("localhost", 8080), false);
    }

    @After
    public void tearDown() {
        Vertx vertx = this.vertx;
        if (vertx != null) {
            vertx.close();
        }
    }

    @Test
    public void testConnect(TestContext context) {
        Async async = context.async();
        client.connect()
                .doOnComplete(async::complete)
                .doOnError(context::fail)
                .subscribe();
    }

    @Test
    public void testLogin(TestContext context) {
        Async async = context.async();
        client.connect()
                .andThen(client.login("username", "password", "clientId", "clientSecret"))
                .doOnSuccess(response -> {
                    checkResponseSuccess(response);
                    async.complete();
                })
                .doOnError(context::fail)
                .subscribe();

    }

    private void checkResponseSuccess(LoginResponse response) {
        if (response.error() != null) {
            Assert.fail("Response error: " + response.error());
        }
    }
}
