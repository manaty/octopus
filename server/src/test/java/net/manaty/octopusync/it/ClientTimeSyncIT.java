package net.manaty.octopusync.it;

import io.grpc.Status;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.RunTestOnContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.ext.jdbc.JDBCClient;
import io.vertx.reactivex.ext.sql.SQLClient;
import net.manaty.octopusync.api.*;
import net.manaty.octopusync.it.fixture.ServerTestBase;
import net.manaty.octopusync.it.fixture.TestRuntimeFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RunWith(VertxUnitRunner.class)
public class ClientTimeSyncIT extends ServerTestBase {
    private static final Logger LOGGER = LoggerFactory.getLogger(ClientTimeSyncIT.class);

    @Rule
    public RunTestOnContext vertxRule = new RunTestOnContext();

    private Vertx vertx;
    private SQLClient sqlClient;

    @Before
    public void setUp() {
        vertx = new Vertx(vertxRule.vertx());
        sqlClient = JDBCClient.createNonShared(
                vertx,
                new JsonObject()
                        .put("url", TestRuntimeFactory.buildJdbcUrl(db, "octopus"))
                        .put("user", db.username())
                        .put("password", db.password()));
    }

    @After
    public void tearDown() {
        SQLClient sqlClient = this.sqlClient;
        if (sqlClient != null) {
            sqlClient.close();
        }

        Vertx vertx = this.vertx;
        if (vertx != null) {
            vertx.close();
        }
    }

    @Test
    public void testSync_WithoutSession(TestContext context) {
        Async async = context.async();
        server.vertxStub().sync(exchange -> {
            exchange.handler(message -> context.fail("Unexpected message from server"));
            exchange.exceptionHandler(e -> {
                Status status = Status.fromThrowable(e);
                context.assertEquals(Status.INVALID_ARGUMENT.getCode(), status.getCode());
                async.complete();
            });
            exchange.write(ClientSyncMessage.getDefaultInstance());
        });
    }

    @Test
    public void testSync(TestContext context) {
        Session session = server.blockingGrpcStub()
                .createSession(CreateSessionRequest.newBuilder().setHeadsetCode("H1").build())
                .getSession();

        server.vertxStub().sync(exchange -> {
            exchange.handler(message -> {
                switch (message.getMessageCase()) {
                    case SYNC_TIME_REQUEST: {
                        exchange.write(ClientSyncMessage.newBuilder()
                                .setSyncTimeResponse(SyncTimeResponse.newBuilder()
                                        .setSeqnum(message.getSyncTimeRequest().getSeqnum())
                                        .setReceivedTimeUtc(System.currentTimeMillis())
                                        .build())
                                .build());
                        break;
                    }
                    case NOTIFICATION: {
                        // ignore
                        break;
                    }
                    default: {
                        context.fail("Unexpected message from server: " + message);
                        break;
                    }
                }
            });
            exchange.exceptionHandler(e -> context.fail(new IllegalStateException("Unexpected exchange error", e)));
            exchange.write(ClientSyncMessage.newBuilder().setSession(session).build());
        });

        scheduleResultLookup(context.async(), context);
    }

    private void scheduleResultLookup(Async async, TestContext context) {
        String query = "SELECT COUNT(1) FROM octopus.client_time_sync_result";
        vertx.setTimer(1000, it -> {
            sqlClient.rxQuery(query)
                    .subscribe(rs -> {
                        int rowCount = rs.getResults().iterator().next().getInteger(0);
                        if (rowCount == 3) {
                            async.complete();
                        } else {
                            LOGGER.info("Query {} yielded {}, will re-schedule...", query, rowCount);
                            scheduleResultLookup(async, context);
                        }
                    }, context::fail);
        });
    }
}
