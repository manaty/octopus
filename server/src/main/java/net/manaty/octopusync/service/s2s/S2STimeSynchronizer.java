package net.manaty.octopusync.service.s2s;

import io.grpc.ManagedChannel;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.processors.PublishProcessor;
import io.vertx.reactivex.core.Future;
import io.vertx.reactivex.core.Vertx;
import net.manaty.octopusync.api.SyncTimeResponse;
import net.manaty.octopusync.model.S2STimeSyncResult;
import net.manaty.octopusync.s2s.api.OctopuSyncS2SGrpc;
import net.manaty.octopusync.s2s.api.OctopuSyncS2SGrpc.OctopuSyncS2SVertxStub;
import net.manaty.octopusync.service.grpc.ManagedChannelFactory;
import net.manaty.octopusync.service.sync.SyncRequestResponseExchange;
import net.manaty.octopusync.service.sync.SyncRequestResponseExchangeFactory;
import net.manaty.octopusync.service.sync.Synchronizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Consumer;

public class S2STimeSynchronizer {
    private static final Logger LOGGER = LoggerFactory.getLogger(S2STimeSynchronizer.class);

    private final Vertx vertx;
    private final NodeListFactory nodeListFactory;
    private final ManagedChannelFactory channelFactory;
    private final Duration nodeLookupInterval;
    private final Duration nodeSyncInterval;
    private final InetSocketAddress localAddress;

    private final ConcurrentMap<InetSocketAddress, Synchronizer<S2STimeSyncResult>> synchronizersByNode;

    private boolean started;
    private long timerId;
    private PublishProcessor<S2STimeSyncResult> resultProcessor;

    public S2STimeSynchronizer(
            Vertx vertx,
            NodeListFactory nodeListFactory,
            ManagedChannelFactory channelFactory,
            Duration nodeLookupInterval,
            Duration nodeSyncInterval,
            InetSocketAddress localAddress) {
        this.vertx = vertx;
        this.nodeListFactory = nodeListFactory;
        this.channelFactory = channelFactory;
        this.nodeLookupInterval = nodeLookupInterval;
        this.nodeSyncInterval = nodeSyncInterval;
        this.localAddress = localAddress;
        this.synchronizersByNode = new ConcurrentHashMap<>();
    }

    public synchronized Observable<S2STimeSyncResult> startSync() {
        if (started) {
            return Observable.fromPublisher(resultProcessor);
        }

        resultProcessor = PublishProcessor.create();
        scheduleSync();
        started = true;
        return Observable.fromPublisher(resultProcessor);
    }

    private synchronized void scheduleSync() {
        scheduleSync(1, nodeLookupInterval.toMillis());
    }

    private synchronized void scheduleSync(long delayMillis, long nextDelayMillis) {
        timerId = vertx.setTimer(delayMillis, it -> {
            loadNodes()
                    .flatMap(nodeAddress -> syncNode(nodeAddress)
//                            .doOnError(e -> LOGGER.error("Failed to sync with node " + nodeAddress, e))
//                            // do not fail exchanges with other nodes
//                            .onErrorResumeNext(Observable.empty()))
                    )
                    .doAfterTerminate(() -> scheduleSync(nextDelayMillis, nextDelayMillis))
                    .forEach(syncResult -> {
                        synchronized (S2STimeSynchronizer.this) {
                            if (started) {
                                resultProcessor.onNext(syncResult);
                            }
                        }
                    });
        });
    }

    private Observable<InetSocketAddress> loadNodes() {
        return Single.fromCallable(() -> {
            List<InetSocketAddress> nodes = nodeListFactory.get();
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Loaded list of nodes: {}", nodes);
            }
            return nodes;
        }).flatMapObservable(Observable::fromIterable);
    }

    private synchronized Observable<S2STimeSyncResult> syncNode(InetSocketAddress remoteAddress) {
        if (started) {
            return synchronizersByNode.computeIfAbsent(remoteAddress, this::createSynchronizer)
                    .startSync();
        } else {
            return Observable.empty();
        }
    }

    private Synchronizer<S2STimeSyncResult> createSynchronizer(InetSocketAddress remoteAddress) {
        ManagedChannel channel = channelFactory.createPlaintextChannel(remoteAddress.getHostName(), remoteAddress.getPort());
        OctopuSyncS2SVertxStub stub = OctopuSyncS2SGrpc.newVertxStub(channel);
        S2STimeSyncResultBuilder resultBuilder = S2STimeSyncResultBuilder.builder(localAddress, remoteAddress);

        SyncRequestResponseExchangeFactory exchangeFactory = (handler, exceptionHandler) ->
                getExchange(stub, handler, exceptionHandler);

        return new Synchronizer<>(exchangeFactory, resultBuilder, nodeSyncInterval);
    }

    private SyncRequestResponseExchange getExchange(
            OctopuSyncS2SVertxStub stub,
            Consumer<SyncTimeResponse> handler,
            Consumer<Throwable> exceptionHandler) {

        Future<SyncRequestResponseExchange> future = Future.future();
        stub.syncTime(exchange -> {
            exchange.handler(handler::accept);
            exchange.exceptionHandler(exceptionHandler::accept);
            future.complete(SyncRequestResponseExchange.wrap(exchange));
        });
        return future.rxSetHandler().blockingGet();
    }

    public synchronized void stopSync() {
        vertx.cancelTimer(timerId);
        synchronizersByNode.values().forEach(Synchronizer::stopSync);
        resultProcessor.onComplete();
        resultProcessor = null;
        started = false;
    }
}

