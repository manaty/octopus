package net.manaty.octopusync.service.client;

import net.manaty.octopusync.model.ClientTimeSyncResult;
import net.manaty.octopusync.service.sync.SyncResultBuilder;

import javax.annotation.Nullable;
import java.util.Objects;

public class ClientTimeSyncResultBuilder extends SyncResultBuilder<ClientTimeSyncResultBuilder, ClientTimeSyncResult> {

    public static ClientTimeSyncResultBuilder builder(String headsetId) {
        return new ClientTimeSyncResultBuilder(headsetId, 0);
    }

    private final String headsetId;

    private ClientTimeSyncResultBuilder(String headsetId, long round) {
        super(round);
        this.headsetId = Objects.requireNonNull(headsetId);
    }

    public ClientTimeSyncResultBuilder newBuilderForRound(long round) {
        return new ClientTimeSyncResultBuilder(headsetId, round);
    }

    @Override
    protected ClientTimeSyncResult buildResult(long round, long finished, long delay, @Nullable String errorMessage) {
        return new ClientTimeSyncResult(headsetId, round, finished, delay, errorMessage);
    }

    @Override
    public String getTargetDescription() {
        return headsetId;
    }
}
