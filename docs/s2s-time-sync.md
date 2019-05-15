# Server-to-server (S2S) time synchronization

## Rationale

S2S time sync serves the purpose of being able to correlate timestamped events from two databases, each of which is maintained by a separate server instance. Servers may have different system time, so to tell, if two events from different databases happened simultaneously or one after another, we need to know the time difference between the two servers.

## Mechanism

#### Master server discovery

At fixed time intervals, each slave server performs a lookup for master server. It does so by using a `Supplier<InetSocketAddress>` instance, which was constructed at server startup based on the configuration. `Supplier<InetSocketAddress>` is a flexible mechanism, that allows the master server to be dynamically discovered. Currently, the only implementation is a static address in the configuration, but it can easily be extended, for instance, to use a shared database or file, which would be dynamically updated by the master server on startup and shutdown.

#### Synchronization rounds

At fixed time intervals, each slave server synchronizes itself against its' master server. It does so by establishing a gRPC bi-directional message stream with the master and performing ping-pong messaging in the following fashion:

```
seqnum = 0
min_samples = 10
max_samples = 100
threshold = 1.0

channel.on_receive = (message) => {
    stddev.adjust(sent - message.received)
    
    if (seqnum > min_samples && stddev < threshold)
      success()
    else if (seqnum == max_samples)
      fail()
    else
      sample()
}

sample = () => {
  seqnum = seqnum + 1
  sent = now()
  message.seqnum = seqnum
  send(message)
}
```

So, during each round of synchronization with the master the slave server makes from 10 to 100 requests, each paired by a response from the master. For each request-response pair it calculates the delta between sending and receiving times and adjusts the running standard deviation (unbiased). After 10 requests have been performed, it begins to check, if the standard deviation is less than a threshold. We use 1.0 as a threshold, because we need sub-millisecond precision. If 100 requests have been made and responded to, and the standard deviation is still bigger than threshold, then the synchronization round ends with a failure.

#### Why synchronize more than once?

Time drift in modern operating systems can accumulate to several seconds per day. Thus, when you need a sub-millisecond precision for time difference on a relatively large time interval (e.g. half an hour), performing synchronization continuously and having measurements for small periods of time (like, each few seconds) is necessary, as opposed to sync'ing at startup and treating the time difference as being constant from there on.

## Data model

The result of each synchronization round is logged into the database. Each entry looks as follows:

```
local_address       VARCHAR(255),
remote_address      VARCHAR(255),
round               BIGINT,
finished_time_utc   BIGINT,
delay_millis        BIGINT,
error               TEXT
```

`local_address`: address of the slave server's gRPC socket
`remote_address`: address of the master server
`round`: number of the synchronization round; incremented sequentially for each new round
`finished_time_utc`: timestamp of the moment the round finished (either successfully or with a failure; in epoch millis)
`delay_millis`: deduced difference in time between slave and master in milliseconds (relative to slave; which means it's them time it takes to deliver slave's message to master)
`error`: description of the failure; if this attribute is not null, then the round ended with a failure, and `delay_millis` must not be taken into consideration

## Configuration

Currently, the following aspects of S2S sychronization can be configured:

- time interval, at which lookup for master server is performed by slave server
- time interval, at which synchronization rounds with master server are performed by slave server
- static master server address

```
grpc:
  master:
    type: static
    address: 192.168.1.2:9991
  masterLookupIntervalMillis: 5000
  masterSyncIntervalMillis: 60000
```

## Remaining questions

- using real time vs system timer (`System.currentTimeMillis()` vs `System.nanoTime()`)