package net.manaty.octopusync.service.db;

import io.reactivex.Completable;
import io.vertx.core.json.JsonArray;
import io.vertx.reactivex.core.Future;
import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.ext.jdbc.JDBCClient;
import io.vertx.reactivex.ext.sql.SQLClient;
import net.manaty.octopusync.model.*;
import net.manaty.octopusync.service.common.LazySupplier;
import net.manaty.octopusync.service.emotiv.event.CortexEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static net.manaty.octopusync.service.common.LazySupplier.lazySupplier;

public class JdbcStorage implements Storage {
    private static final Logger LOGGER = LoggerFactory.getLogger(JdbcStorage.class);

    private static final String S2S_TIME_SYNC_RESULT_INSERT;
    private static final String S2S_TIME_SYNC_RESULT_SELECT_INTERVAL;
    private static final String EEG_EVENT_INSERT;
    private static final String EEG_EVENT_SELECT_INTERVAL;
    private static final String MOOD_STATE_INSERT;
    private static final String MOOD_STATE_SELECT_INTERVAL;
    private static final String CLIENT_TIME_SYNC_RESULT_INSERT;
    private static final String CLIENT_TIME_SYNC_SELECT_INTERVAL;
    private static final String HEADSET_IDS_IN_EEG_EVENTS_SELECT;
    private static final String TRIGGER_INSERT;
    private static final String TRIGGER_SELECT_INTERVAL;
    private static final String MOT_EVENT_INSERT;
    private static final String MOT_EVENT_SELECT_INTERVAL;

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

        S2S_TIME_SYNC_RESULT_SELECT_INTERVAL =
                "SELECT local_address," +
                        " remote_address," +
                        " round," +
                        " finished_time_utc," +
                        " delay_millis," +
                        " error" +
                        " FROM s2s_time_sync_result" +
                        " WHERE finished_time_utc BETWEEN ? and ?" +
                        " ORDER BY finished_time_utc";

        EEG_EVENT_INSERT =
                "INSERT INTO eeg_event " +
                        "(headset_id," +
                        " sid," +
                        " event_time," +
                        " event_time_local," +
                        " event_time_relative," +
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
                        " VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?);";

        EEG_EVENT_SELECT_INTERVAL =
                "SELECT headset_id," +
                        " sid," +
                        " event_time," +
                        " event_time_local," +
                        " event_time_relative," +
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
                        " marker" +
                        " FROM eeg_event" +
                        " WHERE headset_id = ? AND event_time BETWEEN ? AND ?" +
                        " ORDER BY event_time";

        MOOD_STATE_INSERT = "INSERT INTO mood_state " +
                "(headset_id," +
                " since_time_utc," +
                " state)" +
                " VALUES (?,?,?);";

        MOOD_STATE_SELECT_INTERVAL =
                "SELECT headset_id," +
                        " state," +
                        " since_time_utc" +
                        " FROM mood_state" +
                        " WHERE headset_id = ? AND since_time_utc BETWEEN ? AND ?" +
                        " ORDER BY since_time_utc";

        CLIENT_TIME_SYNC_RESULT_INSERT = "INSERT INTO client_time_sync_result " +
                "(headset_id," +
                " round," +
                " finished_time_utc," +
                " delay_millis," +
                " error)" +
                " VALUES (?,?,?,?,?)";

        CLIENT_TIME_SYNC_SELECT_INTERVAL =
                "SELECT headset_id," +
                        " round," +
                        " finished_time_utc," +
                        " delay_millis," +
                        " error" +
                        " FROM client_time_sync_result" +
                        " WHERE finished_time_utc BETWEEN ? AND ?" +
                        " ORDER BY finished_time_utc";

        HEADSET_IDS_IN_EEG_EVENTS_SELECT =
                "SELECT DISTINCT headset_id" +
                        " FROM eeg_event";

        TRIGGER_INSERT = "INSERT INTO trigger" +
                "(happened_time_utc," +
                " message)" +
                " VALUES (?,?)";

        TRIGGER_SELECT_INTERVAL =
                "SELECT id," +
                        " happened_time_utc," +
                        " message" +
                        " FROM trigger" +
                        " WHERE happened_time_utc BETWEEN ? AND ?" +
                        " ORDER BY happened_time_utc";

        MOT_EVENT_INSERT =
                "INSERT INTO mot_event " +
                        "(headset_id," +
                        " sid," +
                        " event_time," +
                        " counter," +
                        " q0," +
                        " q1," +
                        " q2," +
                        " q3," +
                        " accx," +
                        " accy," +
                        " accz," +
                        " magx, " +
                        " magy," +
                        " magz)" +
                        " VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?);";

        MOT_EVENT_SELECT_INTERVAL =
                "SELECT headset_id," +
                        " sid," +
                        " event_time," +
                        " counter," +
                        " q0," +
                        " q1," +
                        " q2," +
                        " q3," +
                        " accx," +
                        " accy," +
                        " accz," +
                        " magx, " +
                        " magy," +
                        " magz" +
                        " FROM mot_event" +
                        " WHERE headset_id = ? AND event_time BETWEEN ? AND ?" +
                        " ORDER BY event_time";
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
                .add(syncResult.getDelay());

        String error = syncResult.getError();
        if (error == null) {
            params.addNull();
        } else {
            params.add(error);
        }

        return sqlClient.get().rxQueryWithParams(S2S_TIME_SYNC_RESULT_INSERT, params)
                .doOnError(e -> {
                    LOGGER.error("Failed to persist S2S time sync result: " + syncResult, e);
                })
                .ignoreElement();
    }

    @Override
    public Stream<S2STimeSyncResult> getS2SSyncResults(long from, long to) {
        Function<JsonArray, S2STimeSyncResult> mapper = item -> new S2STimeSyncResult(
                item.getString(0),
                item.getString(1),
                item.getLong(2),
                item.getLong(3),
                item.getLong(4),
                item.getString(5));

        JsonArray params = new JsonArray()
                .add(from)
                .add(to);

        return getItemsForQuery(S2S_TIME_SYNC_RESULT_SELECT_INTERVAL, params, mapper);
    }

    @Override
    public Completable saveEegEvents(List<EegEvent> events) {
        Function<EegEvent, JsonArray> mapper = (event) ->
                new JsonArray()
                        .add(event.getHeadsetId())
                        .add(event.getSid())
                        .add(event.getTime())
                        .add(event.getTimeLocal())
                        .add(event.getTimeRelative())
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
                        .add(event.getMarker());
        return saveEvents(events, mapper, EEG_EVENT_INSERT);
    }

    @Override
    public Stream<EegEvent> getEegEvents(String headsetId, long from, long to) {
        Function<JsonArray, EegEvent> mapper = item -> {
            EegEvent event = new EegEvent();
            event.setHeadsetId(item.getString(0));
            event.setSid(item.getString(1));
            event.setTime(item.getLong(2));
            event.setTimeLocal(item.getLong(3));
            event.setTimeRelative(item.getLong(4));
            event.setCounter(item.getLong(5));
            event.setInterpolated(item.getBoolean(6));
            event.setSignalQuality(toExactDouble(item.getFloat(7)));
            event.setAf3(toExactDouble(item.getFloat(8)));
            event.setF7(toExactDouble(item.getFloat(9)));
            event.setF3(toExactDouble(item.getFloat(10)));
            event.setFc5(toExactDouble(item.getFloat(11)));
            event.setT7(toExactDouble(item.getFloat(12)));
            event.setP7(toExactDouble(item.getFloat(13)));
            event.setO1(toExactDouble(item.getFloat(14)));
            event.setO2(toExactDouble(item.getFloat(15)));
            event.setP8(toExactDouble(item.getFloat(16)));
            event.setT8(toExactDouble(item.getFloat(17)));
            event.setFc6(toExactDouble(item.getFloat(18)));
            event.setF4(toExactDouble(item.getFloat(19)));
            event.setF8(toExactDouble(item.getFloat(20)));
            event.setAf4(toExactDouble(item.getFloat(21)));
            event.setMarkerHardware(item.getInteger(22));
            event.setMarkerHardware(item.getInteger(23));
            return event;
        };

        JsonArray params = new JsonArray()
                .add(headsetId)
                .add(from)
                .add(to);

        return getItemsForQuery(EEG_EVENT_SELECT_INTERVAL, params, mapper);
    }

    private static double toExactDouble(float f) {
        return Double.parseDouble(Float.toString(f));
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
    public Stream<MoodState> getMoodStates(String headsetId, long from, long to) {
        Function<JsonArray, MoodState> mapper = item -> new MoodState(
                item.getString(0),
                item.getString(1),
                item.getLong(2));

        JsonArray params = new JsonArray()
                .add(headsetId)
                .add(from)
                .add(to);

        return getItemsForQuery(MOOD_STATE_SELECT_INTERVAL, params, mapper);
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

    @Override
    public Stream<ClientTimeSyncResult> getClientSyncResults(String headsetId, long from, long to) {
        Function<JsonArray, ClientTimeSyncResult> mapper = item -> new ClientTimeSyncResult(
                item.getString(0),
                item.getLong(1),
                item.getLong(2),
                item.getLong(3),
                item.getString(4));

        JsonArray params = new JsonArray()
                .add(headsetId)
                .add(from)
                .add(to);

        return getItemsForQuery(CLIENT_TIME_SYNC_SELECT_INTERVAL, params, mapper);
    }

    @Override
    public Set<String> getHeadsetIdsFromEegEvents() {
        return sqlClient.get().rxQuery(HEADSET_IDS_IN_EEG_EVENTS_SELECT)
                .blockingGet()
                .getResults().stream()
                .map(item -> item.getString(0))
                .collect(Collectors.toSet());
    }

    @Override
    public Completable saveTrigger(Trigger trigger) {
        JsonArray params = new JsonArray()
                .add(trigger.getHappenedTimeMillisUtc())
                .add(trigger.getMessage());

        return sqlClient.get().rxQueryWithParams(TRIGGER_INSERT, params)
                .doOnError(e -> {
                    LOGGER.error("Failed to persist trigger: " + trigger, e);
                })
                .ignoreElement();
    }

    @Override
    public Stream<Trigger> getTriggers(long from, long to) {
        Function<JsonArray, Trigger> mapper = item -> new Trigger(
                item.getInteger(0),
                item.getLong(1),
                item.getString(2));

        JsonArray params = new JsonArray()
                .add(from)
                .add(to);

        return getItemsForQuery(TRIGGER_SELECT_INTERVAL, params, mapper);
    }

    @Override
    public Completable saveMotEvents(List<MotEvent> events) {
        Function<MotEvent, JsonArray> mapper = (event) ->
                new JsonArray()
                        .add(event.getHeadsetId())
                        .add(event.getSid())
                        .add(event.getTime())
                        .add(event.getCounter())
                        .add(event.getQ0())
                        .add(event.getQ1())
                        .add(event.getQ2())
                        .add(event.getQ3())
                        .add(event.getAccx())
                        .add(event.getAccy())
                        .add(event.getAccz())
                        .add(event.getMagx())
                        .add(event.getMagy())
                        .add(event.getMagz());
        return saveEvents(events, mapper, MOT_EVENT_INSERT);
    }

    @Override
    public Stream<MotEvent> getMotEvents(String headsetId, long from, long to) {
        Function<JsonArray, MotEvent> mapper = item -> {
            MotEvent event = new MotEvent();
            event.setHeadsetId(item.getString(0));
            event.setSid(item.getString(1));
            event.setTime(item.getLong(2));
            event.setCounter(item.getLong(3));
            event.setQ0(toExactDouble(item.getFloat(4)));
            event.setQ1(toExactDouble(item.getFloat(5)));
            event.setQ2(toExactDouble(item.getFloat(6)));
            event.setQ3(toExactDouble(item.getFloat(7)));
            event.setAccx(toExactDouble(item.getFloat(8)));
            event.setAccy(toExactDouble(item.getFloat(9)));
            event.setAccz(toExactDouble(item.getFloat(10)));
            event.setMagx(toExactDouble(item.getFloat(11)));
            event.setMagy(toExactDouble(item.getFloat(12)));
            event.setMagz(toExactDouble(item.getFloat(13)));
            return event;
        };

        JsonArray params = new JsonArray()
                .add(headsetId)
                .add(from)
                .add(to);

        return getItemsForQuery(MOT_EVENT_SELECT_INTERVAL, params, mapper);
    }

    private <T extends CortexEvent> Completable saveEvents(List<T> events, Function<T, JsonArray> mapper, String insertSql) {
        return sqlClient.get().rxGetConnection()
                .flatMapCompletable(conn -> {
                    List<JsonArray> batch = new ArrayList<>(events.size() + 1);
                    events.forEach(event -> {
                        batch.add(mapper.apply(event));
                    });
                    Future<?> future = Future.future();
                    conn.batchWithParams(insertSql, batch, rs -> {
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

    private <T> Stream<T> getItemsForQuery(String query, JsonArray params, Function<JsonArray, T> mapper) {
        return sqlClient.get().rxQueryWithParams(query, params)
                .blockingGet()
                .getResults().stream()
                .map(mapper);
    }
}
