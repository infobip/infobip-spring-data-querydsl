package com.infobip.flyway;

import org.flywaydb.core.Flyway;
import org.springframework.boot.autoconfigure.flyway.FlywayMigrationStrategy;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;

import java.sql.*;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SqlServerFlywayTestMigrationStrategy implements FlywayMigrationStrategy {

    private static final String DB_URL_PATTERN = "(?<jdbcBaseUrl>.*)/(?<databaseName>.*)";

    private final JdbcTemplate template;
    private final String databaseExistsQuery;
    private final String createDatabaseQuery;

    public SqlServerFlywayTestMigrationStrategy(DataSourceProperties dataSourceProps) {
        Pattern jdbcBaseUrlWithDbNamePattern = Pattern.compile(DB_URL_PATTERN);
        Matcher matcher = jdbcBaseUrlWithDbNamePattern.matcher(dataSourceProps.getUrl());

        if(!matcher.matches()) {
            throw new IllegalArgumentException(dataSourceProps.getUrl() + " does not match " + DB_URL_PATTERN);
        }

        String jdbcBaseUrl = matcher.group("jdbcBaseUrl");
        String databaseName = matcher.group("databaseName");
        databaseExistsQuery = String.format("SELECT count(*) FROM sys.databases WHERE name='%s'", databaseName);
        createDatabaseQuery = String.format("CREATE DATABASE %s", databaseName);
        this.template = new JdbcTemplate(new SimpleDriverDataSource(
                getDriver(jdbcBaseUrl),
                jdbcBaseUrl,
                dataSourceProps.getUsername(),
                dataSourceProps.getPassword()));
    }

    @Override
    public void migrate(Flyway flyway) {

        if (!databaseExists()) {
            createDatabase();
        }
        flyway.clean();
        flyway.migrate();
    }

    private boolean databaseExists() {
        return Objects.equals(template.queryForObject(databaseExistsQuery, Integer.class), 1);
    }

    private void createDatabase() {
        template.execute(createDatabaseQuery);
    }

    private Driver getDriver(String jdbcUrl) {
        try {
            return DriverManager.getDriver(jdbcUrl);
        } catch (SQLException e) {
            throw new IllegalArgumentException(e);
        }
    }
}
