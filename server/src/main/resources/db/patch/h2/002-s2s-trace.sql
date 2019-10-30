CREATE TABLE s2s_time_sync_measurement (
    local_address       VARCHAR(255),
    remote_address      VARCHAR(255),
    round               BIGINT,
    seqnum              BIGINT,
    sent                BIGINT,
    received            BIGINT,
    delta               BIGINT,
    mean                DOUBLE,
    variance_unbiased   DOUBLE,
    stddev              DOUBLE
);

CREATE TABLE client_time_sync_measurement (
    headset_id          VARCHAR(255),
    round               BIGINT,
    seqnum              BIGINT,
    sent                BIGINT,
    received            BIGINT,
    delta               BIGINT,
    mean                DOUBLE,
    variance_unbiased   DOUBLE,
    stddev              DOUBLE
);