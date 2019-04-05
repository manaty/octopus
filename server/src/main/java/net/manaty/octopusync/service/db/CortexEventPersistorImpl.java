package net.manaty.octopusync.service.db;

import io.reactivex.Completable;
import io.vertx.reactivex.core.RxHelper;
import io.vertx.reactivex.core.Vertx;
import net.manaty.octopusync.model.EegEvent;
import net.manaty.octopusync.service.emotiv.event.CortexEvent;
import net.manaty.octopusync.service.emotiv.event.CortexEventKind;
import net.manaty.octopusync.service.emotiv.event.CortexEventVisitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

public class CortexEventPersistorImpl implements CortexEventPersistor, CortexEventVisitor {
    private static final Logger LOGGER = LoggerFactory.getLogger(CortexEventPersistorImpl.class);

    private final Vertx vertx;
    private final Storage storage;
    private final int batchSize;

    private final BlockingQueue<CortexEvent> queue;
    private final ExecutorService executor;

    private final Map<CortexEventKind, List<? extends CortexEvent>> eventsByKind;

    private volatile boolean started;

    public CortexEventPersistorImpl(Vertx vertx, Storage storage, int batchSize) {
        this.vertx = vertx;
        this.storage = storage;
        if (batchSize <= 0) {
            throw new IllegalArgumentException("Invalid batch size: " + batchSize);
        }
        this.batchSize = batchSize;
        this.queue = new LinkedBlockingQueue<>();
        this.executor = Executors.newSingleThreadExecutor(r -> new Thread("cortex-event-persistor"));
        this.eventsByKind = new HashMap<>((int)(CortexEventKind.values().length / 0.75d + 1));
    }

    @Override
    public Completable start() {
        return Completable.fromAction(() -> {
            started = true;
            executor.submit(this::run);
        });
    }

    private void run() {
        while (started) {
            CortexEvent event;
            try {
                event = queue.take();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            event.visitEvent(this);
        }

        @SuppressWarnings("unchecked")
        List<EegEvent> eegEvents = (List<EegEvent>) eventsByKind.get(CortexEventKind.EEG);
        if (eegEvents != null && !eegEvents.isEmpty()) {
            try {
                storage.save(eegEvents).blockingAwait();
            } catch(Exception e){
                LOGGER.error("Failed to save " + eegEvents.size() + "EEG events before shutdown", e);
            }
        }
    }

    @Override
    public void save(CortexEvent event) {
        queue.add(event);
    }

    @Override
    public Completable stop() {
        return Completable.fromAction(() -> {
            started = false;
            executor.shutdown();
            while (!executor.awaitTermination(1, TimeUnit.SECONDS))
                ;
        }).subscribeOn(RxHelper.blockingScheduler(vertx));
    }

    @Override
    public void visitEegEvent(EegEvent event) {
        @SuppressWarnings("unchecked")
        List<EegEvent> events = (List<EegEvent>) eventsByKind.computeIfAbsent(CortexEventKind.EEG, it -> new ArrayList<>());
        events.add(event);
        if (events.size() == batchSize) {
            storage.save(new ArrayList<>(events))
                    .doOnError(e -> {
                        LOGGER.error("Failed to save batch of EEG events", e);
                    })
                    .subscribeOn(RxHelper.blockingScheduler(vertx))
                    .subscribe();
            events.clear();
        }
    }
}
