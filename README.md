# Octopus

OctopuSync platform.

#### Build

```
$ ./gradlew build -x test
```

#### Test

```
$ ./gradlew test -Pembedded_postgres_cache_path=/path/to/cache
```

Integration tests use [yandex-qatools/postgresql-embedded](https://github.com/yandex-qatools/postgresql-embedded), which downloads PostgreSQL binary distribution from the Web. When using a cache folder, the download can be performed only once. You'll need to provide the path to the cache folder via Gradle project parameter `-Pembedded_postgres_cache_path`. It will be created automatically, if necessary, and used to store the PostgreSQL distribution.

#### Deployment

Order of typical deployment is as follows:

1) Create a new schema in PostgreSQL via Liquibase
2) Run the server

###### Setting up Liquibase DB

Refer to the README in `db` module.

###### Running the server

Refer to the README in `server` module.