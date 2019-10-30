package net.manaty.octopusync.service.client;

import net.manaty.octopusync.model.ClientTimeSyncResult;
import net.manaty.octopusync.service.sync.SyncMeasurement;
import net.manaty.octopusync.service.sync.SyncResultBuilder;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;

public class ClientTimeSyncResultBuilder extends SyncResultBuilder<ClientTimeSyncResultBuilder, ClientTimeSyncResult> {

    public static ClientTimeSyncResultBuilder builder(String headsetId, int maxSamples) {
        return new ClientTimeSyncResultBuilder(headsetId, 0, maxSamples);
    }

    private final String headsetId;
    private final int maxSamples;

    private ClientTimeSyncResultBuilder(String headsetId, long round, int maxSamples) {
        super(round, maxSamples);
        this.headsetId = Objects.requireNonNull(headsetId);
        this.maxSamples = maxSamples;
    }

    public ClientTimeSyncResultBuilder newBuilderForRound(long round) {
        return new ClientTimeSyncResultBuilder(headsetId, round, maxSamples);
    }

    @Override
    protected ClientTimeSyncResult buildResult(long round, List<SyncMeasurement> measurements,
                                               long finished, long delay, @Nullable String errorMessage) {
        return new ClientTimeSyncResult(headsetId, round, measurements, finished, delay, errorMessage);
    }

    @Override
    public String getTargetDescription() {
        return headsetId;
    }
}
