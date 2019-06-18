# Octopus

OctopuSync platform.

## Build JAR artifacts

```
$ ./gradlew build -x test
```

## Test

```
$ ./gradlew test -Pembedded_postgres_cache_path=/path/to/cache
```

Integration tests use [yandex-qatools/postgresql-embedded](https://github.com/yandex-qatools/postgresql-embedded), which downloads PostgreSQL binary distribution from the Web. When using a cache folder, the download can be performed only once. You'll need to provide the path to the cache folder via Gradle project parameter `-Pembedded_postgres_cache_path`. It will be created automatically, if necessary, and used to store the PostgreSQL distribution.

## Distribution

```
$ ./gradlew assembleDist
```

This command assembles the server distribution packages in TAR and ZIP formats. Distribution packages are placed in `server/build/distributions` directory.  Distribution structure is as follows:

```
octopus-dist-<version>.zip
|__ bin
|   |__ h2
|   |   |__ ... H2 database files will be placed here at runtime ...
|   |__ octopus.bat
|   |__ octopus.sh
|__ config
|   |__ db-h2.yml
|   |__ db-postgres.yml
|   |__ server.yml
|__ lib
|   |__ server-<version>.jar
|   |__ ... other dependencies ...
|__ LICENSE
|__ README.md
|__ reports
|   |__ ... reports will be output here at runtime ...
|__ site
    |__ ... everything from server/src/main/static directory ...
```

## Deployment

Order of typical deployment is as follows:

1) (Optional) Create a new schema in PostgreSQL via Liquibase
2) Run the server

When using the server distribution package, these steps are managed by `octopus.bat` and `octopus.sh` scripts.

#### Setting up Postgres DB

This step can be skipped by using H2 embeddable database instead. To use H2 database, simply change `--config=server/src/main/dist/config/db-postgres.yml` to `--config=server/src/main/dist/config/db-h2.yml` in Liquibase and Server commands below. The database file will be created in current working directory, from which the command has been run.

###### Create Postgres database and empty schema

Refer to [Installation of development version](https://github.com/manaty/octopus/wiki/Installation-of-development-version#2-configure-the-database) for detailed instructions.

###### Initialize a new database

To initialize Octopus schema in local PostgreSQL database, run `net.manaty.octopusync.Main` (when using an IDE Run configuration) or `server-<version>-all.jar` with the following program arguments:

```
$ java -jar server/build/libs/server-1.0-SNAPSHOT-all.jar --config=server/src/main/dist/config/db-postgres.yml --lb-update --lb-default-schema=octopus
```

###### Drop all objects in an existing database

To clear the database (e.g. before applying a new schema):

```
$ java -jar server/build/libs/server-1.0-SNAPSHOT-all.jar --config=server/src/main/dist/config/db-postgres.yml --lb-drop-all --lb-default-schema=octopus
```

###### Customizing configuration

In `server/src/main/dist/config/db-postgres.yml` it is assumed that:

- PostgreSQL runs on standard port `5432`
- username/password are `postgres/postgres`
- there exists a database named `octopus` and an empty schema named `octopus` inside of it

To use different parameters, override Bootique properties via JVM options:

```
-Dbq.jdbc.octopus.url="jdbc:postgresql://<host>:<port>/<db>?currentSchema=<schema>"
-Dbq.jdbc.octopus.username=<username>
-Dbq.jdbc.octopus.password=<password>
```

#### Running the server

###### Startup

To start a server, run `net.manaty.octopusync.Main` (when using an IDE Run configuration) or `server-<version>-all.jar` with the following program arguments:

```
$ java -jar server/build/libs/server-1.0-SNAPSHOT-all.jar --config=server/src/main/dist/config/server.yml --config=server/src/main/dist/config/db-postgres.yml --octopus-server
```