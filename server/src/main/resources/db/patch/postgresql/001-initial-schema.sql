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
    headset_id          VARCHAR(255),
    sid                 VARCHAR(255),
    event_time          BIGINT,
    event_time_local    BIGINT,
    event_time_relative BIGINT,
    counter             BIGINT,
    interpolated        BOOL,
    signal_quality      NUMERIC,
    af3                 NUMERIC,
    f7                  NUMERIC,
    f3                  NUMERIC,
    fc5                 NUMERIC,
    t7                  NUMERIC,
    p7                  NUMERIC,
    o1                  NUMERIC,
    o2                  NUMERIC,
    p8                  NUMERIC,
    t8                  NUMERIC,
    fc6                 NUMERIC,
    f4                  NUMERIC,
    f8                  NUMERIC,
    af4                 NUMERIC,
    marker_hardware     SMALLINT,
    marker              SMALLINT
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

CREATE TABLE trigger (
    id                SERIAL,
    happened_time_utc BIGINT,
    message           VARCHAR(255)
);

CREATE TABLE mot_event (
    headset_id  VARCHAR(255),
    sid         VARCHAR(255),
    event_time  BIGINT,
    counter     NUMERIC,
    q0          NUMERIC,
    q1          NUMERIC,
    q2          NUMERIC,
    q3          NUMERIC,
    accx        NUMERIC,
    accy        NUMERIC,
    accz        NUMERIC,
    magx        NUMERIC,
    magy        NUMERIC,
    magz        NUMERIC
);

CREATE INDEX s2s_time_sync_result$finished_time_utc ON s2s_time_sync_result (finished_time_utc);
CREATE INDEX eeg_event$event_time ON eeg_event (event_time);
CREATE INDEX mood_state$since_time_utc ON mood_state (since_time_utc);
CREATE INDEX client_time_sync_result$finished_time_utc ON client_time_sync_result (finished_time_utc);
CREATE INDEX trigger$happened_time_utc ON trigger (happened_time_utc);
CREATE INDEX mot_event$event_time ON mot_event (event_time);

CREATE INDEX eeg_event$headset_id ON eeg_event USING HASH (headset_id);
CREATE INDEX mood_state$headset_id ON mood_state USING HASH (headset_id);
CREATE INDEX client_time_sync_result$headset_id ON client_time_sync_result USING HASH (headset_id);
CREATE INDEX mot_event$headset_id ON mot_event USING HASH (headset_id);