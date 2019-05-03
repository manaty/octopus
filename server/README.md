# Octopus server

#### Startup

To start a server, run `net.manaty.octopusync.Main` (when using an IDE Run configuration) or `server-<version>.jar` with the following program arguments:

```
--config=server/config/server-dev.yml --octopus-server
```

E.g.

```
$ java -jar server/build/libs/server-1.0-SNAPSHOT-all.jar --config=server/config/server-dev.yml --octopus-server
```

With regards to the JDBC connection, same assumptions as in `db` module apply. To use a different configuration, refer to the section "Customizing configuration" in the README in `db` module.

#### Customizing configuration

###### JDBC datasource

Refer to the section "Customizing configuration" in the README in `db` module.

###### Creating a cluster of multiple servers

You'll need to create one copy of `server/config/server-dev.yml` configuration for each server.

For each server you'll need to:

- re-define JDBC datasource parameters, so that each server uses its' own database
- customize the list of cluster nodes in `grpc.nodes.addresses` section of configuration

For instance, if the cluster will contain two servers on hosts `192.168.1.2` and `192.168.1.3`, each having its' own local database, then there will be two configurations (logging section is omitted for brevity's sake):

**server1.yml**

```
jdbc:
  octopus:
    url: jdbc:postgresql://localhost:5432/octopus?currentSchema=octopus
    maxActive: 2
    username: postgres
    password: postgres
    
grpc:
  port: 9991
  nodes:
    type: static
    addresses:
      - 192.168.1.3:9991
  nodeSyncIntervalMillis: 1000
```

**server2.yml**

```
jdbc:
  octopus:
    url: jdbc:postgresql://localhost:5432/octopus?currentSchema=octopus
    maxActive: 2
    username: postgres
    password: postgres
    
grpc:
  port: 9991
  nodes:
    type: static
    addresses:
      - 192.168.1.2:9991
  nodeSyncIntervalMillis: 1000
```

Note that the only difference is the contents of `grpc.nodes.addresses` section.