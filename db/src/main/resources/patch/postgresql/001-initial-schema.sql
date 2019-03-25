--liquibase formatted sql

--changeset atomashpolskiy:1
CREATE TABLE s2s_time_sync_result (
    local_address       VARCHAR(255),
    remote_address      VARCHAR(255),
    round               BIGINT,
    finished_time_utc   BIGINT,
    delay_millis        BIGINT,
    error               TEXT
);