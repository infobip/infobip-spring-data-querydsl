package com.infobip.spring.data.common;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;

import java.sql.*;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class DatabaseCreator {

    private final JdbcTemplate template;
    private final String databaseExistsQuery;
    private final String createDatabaseQuery;

    DatabaseCreator(String url, String username, String password) {
        String databaseUrlPattern = getDatabaseUrlPattern(url);
        Pattern jdbcBaseUrlWithDbNamePattern = Pattern.compile(databaseUrlPattern);
        Matcher matcher = jdbcBaseUrlWithDbNamePattern.matcher(url);

        if (!matcher.matches()) {
            throw new IllegalArgumentException(url + " does not match " + databaseUrlPattern);
        }

        String jdbcBaseUrl = matcher.group("jdbcBaseUrl");
        String databaseName = matcher.group("databaseName");
        databaseExistsQuery = String.format("SELECT count(*) FROM sys.databases WHERE name='%s'", databaseName);
        createDatabaseQuery = String.format("CREATE DATABASE %s", databaseName);
        this.template = new JdbcTemplate(
                new SimpleDriverDataSource(getDriver(jdbcBaseUrl), jdbcBaseUrl, username, password));
    }

    private String getDatabaseUrlPattern(String url) {

        if (url.startsWith("jdbc:jtds")) {
            return "(?<jdbcBaseUrl>.*)/(?<databaseName>.*)";
        }

        return "(?<jdbcBaseUrl>.*);database=(?<databaseName>.*)";
    }

    void createDatabaseIfItDoesntExist() {
        if(!databaseExists()) {
            template.execute(createDatabaseQuery);
        }
    }

    private boolean databaseExists() {
        return Objects.equals(template.queryForObject(databaseExistsQuery, Integer.class), 1);
    }

    private Driver getDriver(String jdbcUrl) {
        try {
            return DriverManager.getDriver(jdbcUrl);
        } catch (SQLException e) {
            throw new IllegalArgumentException(e);
        }
    }
}
