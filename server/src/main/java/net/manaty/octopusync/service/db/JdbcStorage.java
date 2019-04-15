package net.manaty.octopusync.service.db;

import io.reactivex.Completable;
import io.vertx.core.json.JsonArray;
import io.vertx.reactivex.core.Future;
import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.ext.jdbc.JDBCClient;
import io.vertx.reactivex.ext.sql.SQLClient;
import net.manaty.octopusync.model.ClientTimeSyncResult;
import net.manaty.octopusync.model.EegEvent;
import net.manaty.octopusync.model.MoodState;
import net.manaty.octopusync.model.S2STimeSyncResult;
import net.manaty.octopusync.service.common.LazySupplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import static net.manaty.octopusync.service.common.LazySupplier.lazySupplier;

public class JdbcStorage implements Storage {
    private static final Logger LOGGER = LoggerFactory.getLogger(JdbcStorage.class);

    private static final String S2S_TIME_SYNC_RESULT_INSERT;
    private static final String EEG_EVENT_INSERT;
    private static final String MOOD_STATE_INSERT;
    private static final String CLIENT_TIME_SYNC_RESULT_INSERT;

    static {
        S2S_TIME_SYNC_RESULT_INSERT =
                "INSERT INTO s2s_time_sync_result " +
                        "(local_address," +
                        " remote_address," +
                        " round," +
                        " finished_time_utc," +
                        " delay_millis," +
                        " error)" +
                        " VALUES (?,?,?,?,?,?)";

        EEG_EVENT_INSERT =
                "INSERT INTO eeg_event " +
                        "(sid," +
                        " event_time," +
                        " counter," +
                        " interpolated," +
                        " signal_quality," +
                        " af3," +
                        " f7," +
                        " f3," +
                        " fc5," +
                        " t7, " +
                        " p7," +
                        " o1," +
                        " o2," +
                        " p8," +
                        " t8," +
                        " fc6," +
                        " f4," +
                        " f8," +
                        " af4," +
                        " marker_hardware," +
                        " marker)" +
                        " VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?);";

        MOOD_STATE_INSERT = "INSERT INTO mood_state " +
                "(headset_id," +
                " since_time_utc," +
                " state)" +
                " VALUES (?,?,?);";

        CLIENT_TIME_SYNC_RESULT_INSERT = "INSERT INTO client_time_sync_result " +
                "(headset_id," +
                " round," +
                " finished_time_utc," +
                " delay_millis," +
                " error)" +
                " VALUES (?,?,?,?,?)";
    }

    private final LazySupplier<SQLClient> sqlClient;

    public JdbcStorage(Vertx vertx, Supplier<DataSource> dataSource) {
        this.sqlClient = lazySupplier(() -> new JDBCClient(io.vertx.ext.jdbc.JDBCClient.create(
                vertx.getDelegate(), dataSource.get())));
    }

    @Override
    public Completable save(S2STimeSyncResult syncResult) {
        JsonArray params = new JsonArray()
                .add(syncResult.getLocalAddress())
                .add(syncResult.getRemoteAddress())
                .add(syncResult.getRound())
                .add(syncResult.getFinished())
                .add(syncResult.getDelay())
                .add(syncResult.getError());

        return sqlClient.get().rxQueryWithParams(S2S_TIME_SYNC_RESULT_INSERT, params)
                .doOnError(e -> {
                    LOGGER.error("Failed to persist S2S time sync result: " + syncResult, e);
                })
                .ignoreElement();
    }

    @Override
    public Completable save(List<EegEvent> events) {
        return sqlClient.get().rxGetConnection()
                .flatMapCompletable(conn -> {
                    List<JsonArray> batch = new ArrayList<>(events.size() + 1);
                    events.forEach(event -> {
                        batch.add(new JsonArray()
                                .add(event.getSid())
                                .add(event.getTime())
                                .add(event.getCounter())
                                .add(event.isInterpolated())
                                .add(event.getSignalQuality())
                                .add(event.getAf3())
                                .add(event.getF7())
                                .add(event.getF3())
                                .add(event.getFc5())
                                .add(event.getT7())
                                .add(event.getP7())
                                .add(event.getO1())
                                .add(event.getO2())
                                .add(event.getP8())
                                .add(event.getT8())
                                .add(event.getFc6())
                                .add(event.getF4())
                                .add(event.getF8())
                                .add(event.getAf4())
                                .add(event.getMarkerHardware())
                                .add(event.getMarker()));
                    });
                    Future<?> future = Future.future();
                    conn.batchWithParams(EEG_EVENT_INSERT, batch, rs -> {
                        if (rs.succeeded()) {
                            future.complete();
                        } else {
                            future.fail(rs.cause());
                        }
                    });
                    return future.rxSetHandler()
                            .doAfterTerminate(conn::close)
                            .ignoreElement();
                });
    }

    @Override
    public Completable save(MoodState moodState) {
        JsonArray params = new JsonArray()
                .add(moodState.getHeadsetId())
                .add(moodState.getSinceTimeUtc())
                .add(moodState.getState());

        return sqlClient.get().rxQueryWithParams(MOOD_STATE_INSERT, params)
                .doOnError(e -> {
                    LOGGER.error("Failed to persist mood state: " + moodState, e);
                })
                .ignoreElement();
    }

    @Override
    public Completable save(ClientTimeSyncResult syncResult) {
        JsonArray params = new JsonArray()
                .add(syncResult.getHeadsetId())
                .add(syncResult.getRound())
                .add(syncResult.getFinished())
                .add(syncResult.getDelay());

        String error = syncResult.getError();
        if (error == null) {
            params.addNull();
        } else {
            params.add(syncResult.getError());
        }

        return sqlClient.get().rxQueryWithParams(CLIENT_TIME_SYNC_RESULT_INSERT, params)
                .doOnError(e -> {
                    LOGGER.error("Failed to persist client time sync result: " + syncResult, e);
                })
                .ignoreElement();
    }
}
