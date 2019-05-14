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

CREATE TABLE eeg_event (
    sid             VARCHAR(255),
    event_time      REAL,
    counter         BIGINT,
    interpolated    BOOL,
    signal_quality  REAL,
    af3             REAL,
    f7              REAL,
    f3              REAL,
    fc5             REAL,
    t7              REAL,
    p7              REAL,
    o1              REAL,
    o2              REAL,
    p8              REAL,
    t8              REAL,
    fc6             REAL,
    f4              REAL,
    f8              REAL,
    af4             REAL,
    marker_hardware SMALLINT,
    marker          SMALLINT
);

CREATE TABLE mood_state (
    headset_id      VARCHAR(255),
    since_time_utc  BIGINT,
    state           VARCHAR(255)
);

CREATE TABLE client_time_sync_result (
    headset_id          VARCHAR(255),
    round               BIGINT,
    finished_time_utc   BIGINT,
    delay_millis        BIGINT,
    error               TEXT
);