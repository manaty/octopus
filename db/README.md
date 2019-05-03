# Liquibase DB

#### Initialize a new database

To create Octopus schema in local PostgreSQL database, run `net.manaty.octopusync.db.LiquibaseMain` (when using an IDE Run configuration) or `db-<version>-all.jar` with the following program arguments:

```
--config=db/config/db-dev.yml --lb-update --lb-default-schema=octopus
```

E.g.

```
$ java -jar db/build/libs/db-1.0-SNAPSHOT-all.jar --config=db/config/db-dev.yml --lb-update --lb-default-schema=octopus
```

#### Drop all objects in an existing database

To clear the database (e.g. before applying a new schema):

```
--config=db/config/db-dev.yml --lb-drop-all --lb-default-schema=octopus
```

#### Customizing configuration

In `db/config/db-dev.yml` it is assumed that:
 
- PostgreSQL runs on standard port `5432`
- username/password are `postgres/postgres`
- there exists a database named `octopus` and an empty schema named `octopus` inside of it

To use different parameters, override Bootique properties via JVM options:

```
-Dbq.jdbc.octopus.url="jdbc:postgresql://<host>:<port>/<db>?currentSchema=<schema>"
-Dbq.jdbc.octopus.username=<username>
-Dbq.jdbc.octopus.password=<password>
```