package net.manaty.octopusync.service.client;

import io.reactivex.Observable;
import io.vertx.grpc.GrpcBidiExchange;
import net.manaty.octopusync.api.ClientSyncMessage;
import net.manaty.octopusync.api.ServerSyncMessage;
import net.manaty.octopusync.model.ClientTimeSyncResult;

public class ClientTimeSynchronizer {

    public ClientTimeSynchronizer(GrpcBidiExchange<ClientSyncMessage, ServerSyncMessage> exchange) {

    }

    public Observable<ClientTimeSyncResult> startSync() {
        return null;
    }

    public void stopSync() {

    }
}
