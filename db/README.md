# Liquibase DB

To create Octopus schema in local PostgreSQL database, run `net.manaty.octopusync.db.LiquibaseMain` (when using an IDE Run configuration) or `db-<version>.jar` with the following program arguments:

```
--config=db/config/db-dev.yml --lb-update --lb-default-schema=octopus
```

It is assumed that the username/password are `postgres/postgres`. To use different credentials, override Bootique properties via JVM options:

```
-Dbq.jdbc.octopus.username=<username>
-Dbq.jdbc.octopus.password=<password>
```

To clear the database (e.g. before applying a new schema):

```
--config=db/config/db-dev.yml --lb-drop-all --lb-default-schema=octopus
```