package net.manaty.octopusync.service.db;

import io.reactivex.Completable;
import io.vertx.core.json.JsonArray;
import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.ext.jdbc.JDBCClient;
import io.vertx.reactivex.ext.sql.SQLClient;
import net.manaty.octopusync.model.S2STimeSyncResult;
import net.manaty.octopusync.service.common.LazySupplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.util.function.Supplier;

import static net.manaty.octopusync.service.common.LazySupplier.lazySupplier;

public class JdbcStorage implements Storage {
    private static final Logger LOGGER = LoggerFactory.getLogger(JdbcStorage.class);

    private final LazySupplier<SQLClient> sqlClient;

    private final String S2S_TIME_SYNC_RESULT_INSERT;

    {
        S2S_TIME_SYNC_RESULT_INSERT =
                "INSERT INTO s2s_time_sync_result " +
                        "(local_address," +
                        " remote_address," +
                        " round," +
                        " finished_time_utc," +
                        " delay_millis," +
                        " error)" +
                        "VALUES (?,?,?,?,?,?)";
    }

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
}
