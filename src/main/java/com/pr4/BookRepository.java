package com.pr4;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Properties;

public class BookRepository {
    private static final String INSERT_SQL = "INSERT INTO books (type, title, author, year, pages, genre, format, file_size, publisher, print_run, duration_minutes, narrator, subject, grade_level, quantity) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

    private final String url;
    private final String user;
    private final String password;

    public BookRepository(String configPath) {
        Properties properties = loadProperties(configPath);
        this.url = getRequiredProperty(properties, "db.url");
        this.user = getRequiredProperty(properties, "db.user");
        this.password = getRequiredProperty(properties, "db.password");
    }

    public void insert(Book book, int quantity) throws SQLException {
        try (Connection connection = DriverManager.getConnection(url, user, password);
             PreparedStatement statement = connection.prepareStatement(INSERT_SQL)) {
            statement.setString(1, book.getClass().getSimpleName());
            statement.setString(2, book.getTitle());
            statement.setString(3, book.getAuthor());
            statement.setInt(4, book.getYear());
            statement.setInt(5, book.getPages());
            statement.setString(6, book.getGenre().name());
            fillSubtypeFields(statement, book);
            statement.setInt(15, quantity);
            statement.executeUpdate();
        }
    }

    private static void fillSubtypeFields(PreparedStatement statement, Book book) throws SQLException {
        statement.setNull(7, Types.VARCHAR);
        statement.setNull(8, Types.DOUBLE);
        statement.setNull(9, Types.VARCHAR);
        statement.setNull(10, Types.INTEGER);
        statement.setNull(11, Types.INTEGER);
        statement.setNull(12, Types.VARCHAR);
        statement.setNull(13, Types.VARCHAR);
        statement.setNull(14, Types.INTEGER);

        if (book instanceof EBook value) {
            statement.setString(7, value.getFormat());
            statement.setDouble(8, value.getFileSize());
            return;
        }
        if (book instanceof PaperBook value) {
            statement.setString(9, value.getPublisher());
            statement.setInt(10, value.getPrintRun());
            return;
        }
        if (book instanceof AudioBook value) {
            statement.setInt(11, value.getDurationMinutes());
            statement.setString(12, value.getNarrator());
            return;
        }
        if (book instanceof TextBook value) {
            statement.setString(13, value.getSubject());
            statement.setInt(14, value.getGradeLevel());
        }
    }

    private static Properties loadProperties(String configPath) {
        Path path = Path.of(configPath);
        Properties properties = new Properties();
        try (InputStream inputStream = Files.newInputStream(path)) {
            properties.load(inputStream);
            return properties;
        } catch (IOException e) {
            throw new IllegalArgumentException("Cannot read config file: " + path, e);
        }
    }

    private static String getRequiredProperty(Properties properties, String key) {
        String value = properties.getProperty(key);
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Missing required property: " + key);
        }
        return value.trim();
    }
}
