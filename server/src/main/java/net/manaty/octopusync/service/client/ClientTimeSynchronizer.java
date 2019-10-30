package net.manaty.octopusync.service.client;

import io.grpc.Status;
import io.reactivex.Observable;
import io.vertx.grpc.GrpcBidiExchange;
import net.manaty.octopusync.api.ClientSyncMessage;
import net.manaty.octopusync.api.ServerSyncMessage;
import net.manaty.octopusync.api.SyncTimeRequest;
import net.manaty.octopusync.api.SyncTimeResponse;
import net.manaty.octopusync.model.ClientTimeSyncResult;
import net.manaty.octopusync.service.sync.SyncRequestResponseExchange;
import net.manaty.octopusync.service.sync.Synchronizer;

import java.time.Duration;
import java.util.function.Consumer;

public class ClientTimeSynchronizer {

    private final GrpcBidiExchange<ClientSyncMessage, ServerSyncMessage> exchange;
    private final Synchronizer<ClientTimeSyncResult> synchronizer;

    private volatile Consumer<SyncTimeResponse> syncTimeResponseHandler;
    private volatile Consumer<Throwable> exceptionHandler;

    public ClientTimeSynchronizer(
            String headsetId,
            GrpcBidiExchange<ClientSyncMessage, ServerSyncMessage> exchange,
            Duration syncInterval,
            double devThreshold,
            int minSamples,
            int maxSamples) {

        this.exchange = exchange;
        this.synchronizer = new Synchronizer<>(
                this::updateExchangeHandlers, ClientTimeSyncResultBuilder.builder(headsetId, maxSamples), syncInterval,
                devThreshold, minSamples, maxSamples);
    }

    private SyncRequestResponseExchange updateExchangeHandlers(
            Consumer<SyncTimeResponse> syncTimeResponseHandler, Consumer<Throwable> exceptionHandler) {

        synchronized (this) {
            this.syncTimeResponseHandler = syncTimeResponseHandler;
            this.exceptionHandler = exceptionHandler;
        }

        return new SyncRequestResponseExchange() {
            @Override
            public void write(SyncTimeRequest request) {
                exchange.write(ServerSyncMessage.newBuilder()
                        .setSyncTimeRequest(request)
                        .build());
            }

            @Override
            public void end() {
                // ignore synchronizer's request to end the exchange,
                // as it will be re-used in the next round
            }

            @Override
            public void fail(Throwable e) {
                exchange.fail(e);
            }
        };
    }

    public Observable<ClientTimeSyncResult> startSync() {
        return Observable.defer(() -> {
            exchange.handler(this::handleMessage);
            return synchronizer.startSync();
        });
    }

    private void handleMessage(ClientSyncMessage message) {
        ClientSyncMessage.MessageCase messageCase = message.getMessageCase();
        switch (messageCase) {
            case SESSION: {
                exchange.fail(Status.FAILED_PRECONDITION
                        .withDescription("Sync exchange is already extablished")
                        .asRuntimeException());
                break;
            }
            case SYNC_TIME_RESPONSE: {
                synchronized (this) {
                    Consumer<SyncTimeResponse> syncTimeResponseHandler = this.syncTimeResponseHandler;
                    if (syncTimeResponseHandler != null) {
                        syncTimeResponseHandler.accept(message.getSyncTimeResponse());
                    }
                }
                break;
            }
            case MESSAGE_NOT_SET: {
                exchange.fail(Status.INVALID_ARGUMENT
                        .withDescription("Empty message")
                        .asRuntimeException());
                break;
            }
            default: {
                exchange.fail(Status.UNKNOWN
                        .withDescription("Unexpected server error: unknown message case " + messageCase)
                        .asRuntimeException());
                break;
            }
        }
    }

    public void stopSync() {
        synchronizer.stopSync();
    }

    public synchronized void onExchangeError(Throwable e) {
        Consumer<Throwable> exceptionHandler = this.exceptionHandler;
        if (exceptionHandler != null) {
            exceptionHandler.accept(e);
        }
    }
}
