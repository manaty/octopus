package net.manaty.octopusync.it.fixture.db;

import io.reactivex.Completable;
import net.manaty.octopusync.model.ClientTimeSyncResult;
import net.manaty.octopusync.model.EegEvent;
import net.manaty.octopusync.model.MoodState;
import net.manaty.octopusync.model.S2STimeSyncResult;
import net.manaty.octopusync.service.db.Storage;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

public class InMemoryStorage implements Storage {

    private final long delayBeforeSaveMillis;

    private final Queue<S2STimeSyncResult> s2sTimeSyncResults;
    private final Queue<EegEvent> eegEvents;
    private final Queue<MoodState> moodStates;
    private final Queue<ClientTimeSyncResult> clientTimeSyncResults;

    public InMemoryStorage(long delayBeforeSaveMillis) {
        this.delayBeforeSaveMillis = delayBeforeSaveMillis;
        this.s2sTimeSyncResults = new ConcurrentLinkedQueue<>();
        this.eegEvents = new ConcurrentLinkedQueue<>();
        this.moodStates = new ConcurrentLinkedQueue<>();
        this.clientTimeSyncResults = new ConcurrentLinkedQueue<>();
    }

    @Override
    public Completable save(S2STimeSyncResult syncResult) {
        return Completable.fromAction(() -> {
            s2sTimeSyncResults.add(syncResult);
        }).delay(delayBeforeSaveMillis, TimeUnit.MILLISECONDS);
    }

    @Override
    public Stream<S2STimeSyncResult> getS2SSyncResults(long from, long to) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Completable save(List<EegEvent> events) {
        return Completable.fromAction(() -> {
            eegEvents.addAll(events);
        }).delay(delayBeforeSaveMillis, TimeUnit.MILLISECONDS);
    }

    @Override
    public Stream<EegEvent> getEegEvents(long from, long to) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Completable save(MoodState moodState) {
        return Completable.fromAction(() -> {
            moodStates.add(moodState);
        }).delay(delayBeforeSaveMillis, TimeUnit.MILLISECONDS);
    }

    @Override
    public Stream<MoodState> getMoodStates(long from, long to) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Completable save(ClientTimeSyncResult syncResult) {
        return Completable.fromAction(() -> {
            clientTimeSyncResults.add(syncResult);
        }).delay(delayBeforeSaveMillis, TimeUnit.MILLISECONDS);
    }

    @Override
    public Stream<ClientTimeSyncResult> getClientSyncResults(long from, long to) {
        throw new UnsupportedOperationException();
    }

    public Queue<S2STimeSyncResult> getS2sTimeSyncResults() {
        return s2sTimeSyncResults;
    }

    public Queue<EegEvent> getEegEvents() {
        return eegEvents;
    }

    public Queue<MoodState> getMoodStates() {
        return moodStates;
    }

    public Queue<ClientTimeSyncResult> getClientTimeSyncResults() {
        return clientTimeSyncResults;
    }
}
