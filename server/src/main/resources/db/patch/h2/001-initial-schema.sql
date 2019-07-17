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
    event_time_relative BIGINT,
    counter             BIGINT,
    interpolated        BOOLEAN,
    signal_quality      DOUBLE,
    af3                 DOUBLE,
    f7                  DOUBLE,
    f3                  DOUBLE,
    fc5                 DOUBLE,
    t7                  DOUBLE,
    p7                  DOUBLE,
    o1                  DOUBLE,
    o2                  DOUBLE,
    p8                  DOUBLE,
    t8                  DOUBLE,
    fc6                 DOUBLE,
    f4                  DOUBLE,
    f8                  DOUBLE,
    af4                 DOUBLE,
    marker_hardware     INT,
    marker              INT
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
    id                IDENTITY,
    happened_time_utc BIGINT,
    message           VARCHAR(255)
);

CREATE TABLE mot_event (
    headset_id  VARCHAR(255),
    sid         VARCHAR(255),
    event_time  BIGINT,
    counter     DOUBLE,
    q0          DOUBLE,
    q1          DOUBLE,
    q2          DOUBLE,
    q3          DOUBLE,
    accx        DOUBLE,
    accy        DOUBLE,
    accz        DOUBLE,
    magx        DOUBLE,
    magy        DOUBLE,
    magz        DOUBLE
);

CREATE INDEX s2s_time_sync_result$finished_time_utc ON s2s_time_sync_result (finished_time_utc);
CREATE INDEX eeg_event$event_time ON eeg_event (event_time);
CREATE INDEX mood_state$since_time_utc ON mood_state (since_time_utc);
CREATE INDEX client_time_sync_result$finished_time_utc ON client_time_sync_result (finished_time_utc);
CREATE INDEX trigger$happened_time_utc ON trigger (happened_time_utc);
CREATE INDEX mot_event$event_time ON mot_event (event_time);

CREATE HASH INDEX eeg_event$headset_id ON eeg_event (headset_id);
CREATE HASH INDEX mood_state$headset_id ON mood_state (headset_id);
CREATE HASH INDEX client_time_sync_result$headset_id ON client_time_sync_result (headset_id);
CREATE HASH INDEX mot_event$headset_id ON mot_event (headset_id);