package net.manaty.octopusync.it;

import io.grpc.Status;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import net.manaty.octopusync.api.CreateSessionRequest;
import net.manaty.octopusync.api.Session;
import net.manaty.octopusync.api.State;
import net.manaty.octopusync.api.UpdateStateRequest;
import net.manaty.octopusync.it.fixture.ServerTestBase;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(VertxUnitRunner.class)
public class UpdateStateIT extends ServerTestBase {

    @Test
    public void testUpdateState_WithoutSession(TestContext context) {
        Async async = context.async();
        server.vertxStub().updateState(UpdateStateRequest.getDefaultInstance(), ar -> {
            if (ar.succeeded()) {
                context.fail("Expected error");
            } else {
                context.assertEquals(Status.INVALID_ARGUMENT.getCode(), Status.fromThrowable(ar.cause()).getCode());
                async.complete();
            }
        });
    }

    @Test
    public void testUpdateState(TestContext context) {
        Session session = server.blockingGrpcStub()
                .createSession(CreateSessionRequest.newBuilder().setHeadsetCode("H1").build())
                .getSession();

        UpdateStateRequest request = UpdateStateRequest.newBuilder()
                .setSession(session)
                .setSinceTimeUtc(System.currentTimeMillis())
                .setState(State.FRISSON_MUSICAL)
                .build();

        Async async = context.async();
        server.vertxStub().updateState(request, ar -> {
            if (ar.succeeded()) {
                async.complete();
            } else {
                context.fail(ar.cause());
            }
        });
    }
}
