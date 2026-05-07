package com.pr4;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

public final class DatabaseConfig {
    private final String url;
    private final String user;
    private final String password;

    public DatabaseConfig(String url, String user, String password) {
        if (isBlank(url)) {
            throw new IllegalArgumentException("Property db.url must not be blank");
        }
        if (isBlank(user)) {
            throw new IllegalArgumentException("Property db.user must not be blank");
        }
        if (password == null) {
            throw new IllegalArgumentException("Property db.password must not be null");
        }

        this.url = url;
        this.user = user;
        this.password = password;
    }

    public static DatabaseConfig fromFile(Path path) {
        if (path == null) {
            throw new IllegalArgumentException("Configuration file path must not be null");
        }

        Properties properties = new Properties();
        try (InputStream inputStream = Files.newInputStream(path)) {
            properties.load(inputStream);
        } catch (IOException e) {
            throw new IllegalArgumentException("Failed to read configuration file: " + e.getMessage(), e);
        }

        String url = properties.getProperty("db.url");
        String user = properties.getProperty("db.user");
        String password = properties.getProperty("db.password");
        return new DatabaseConfig(url, user, password);
    }

    public String getUrl() {
        return url;
    }

    public String getUser() {
        return user;
    }

    public String getPassword() {
        return password;
    }

    private static boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
