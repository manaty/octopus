package net.manaty.octopusync.it.fixture;

import io.bootique.command.CommandOutcome;
import org.junit.rules.ExternalResource;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class TestLiquibaseDb extends ExternalResource {

    private final TestPostgresDb db;
    private final ManagedBQRuntime liquibaseUpdateFactory;
    private final ManagedBQRuntime liquibaseDropAllFactory;

    public TestLiquibaseDb(
            TestPostgresDb db,
            ManagedBQRuntime liquibaseUpdateFactory,
            ManagedBQRuntime liquibaseDropAllFactory) {

        this.db = db;
        this.liquibaseUpdateFactory = liquibaseUpdateFactory;
        this.liquibaseDropAllFactory = liquibaseDropAllFactory;
    }

    @Override
    protected void before() {
        initDb();
        // init Liquibase schema
        runLiquibase(liquibaseUpdateFactory);
    }

    @Override
    protected void after() {
        runLiquibase(liquibaseDropAllFactory);
    }

    private void initDb() {
        String jdbcUrl;
        String sql;

        jdbcUrl = TestRuntimeFactory.buildJdbcUrl(db, "postgres");
        sql = "CREATE DATABASE octopus";
        try (Connection connection = DriverManager.getConnection(jdbcUrl, db.username(), db.password())) {
            connection.createStatement().execute(sql);
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to execute: " + sql, e);
        }

        jdbcUrl = TestRuntimeFactory.buildJdbcUrl(db, "octopus");
        sql = "CREATE SCHEMA octopus";
        try (Connection connection = DriverManager.getConnection(jdbcUrl, db.username(), db.password())) {
            connection.createStatement().execute(sql);
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to execute: " + sql, e);
        }
    }

    private void runLiquibase(ManagedBQRuntime liquibaseFactory) {
        CommandOutcome outcome = liquibaseFactory.run();
        if (!outcome.isSuccess()) {
            throw new IllegalStateException("Failed to run Liquibase: " + outcome.getMessage());
        }
    }
}
