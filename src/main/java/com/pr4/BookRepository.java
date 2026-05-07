package com.pr4;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

public class BookRepository {
    private static final String INSERT_SQL = "INSERT INTO books (type, title, author, year, pages, genre, quantity, format, file_size, publisher, print_run, duration_minutes, narrator, subject, grade_level) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

    private final DatabaseConfig config;

    public BookRepository(DatabaseConfig config) {
        if (config == null) {
            throw new IllegalArgumentException("Database config must not be null");
        }
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException("PostgreSQL JDBC driver is not available in runtime classpath", e);
        }
        this.config = config;
    }

    public void save(Book book, int quantity) throws SQLException {
        if (book == null) {
            throw new IllegalArgumentException("Book must not be null");
        }
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }

        try (Connection connection = DriverManager.getConnection(config.getUrl(), config.getUser(), config.getPassword());
             PreparedStatement statement = connection.prepareStatement(INSERT_SQL)) {
            statement.setString(1, resolveType(book));
            statement.setString(2, book.getTitle());
            statement.setString(3, book.getAuthor());
            statement.setInt(4, book.getYear());
            statement.setInt(5, book.getPages());
            statement.setString(6, book.getGenre().name());
            statement.setInt(7, quantity);

            statement.setNull(8, Types.VARCHAR);
            statement.setNull(9, Types.DOUBLE);
            statement.setNull(10, Types.VARCHAR);
            statement.setNull(11, Types.INTEGER);
            statement.setNull(12, Types.INTEGER);
            statement.setNull(13, Types.VARCHAR);
            statement.setNull(14, Types.VARCHAR);
            statement.setNull(15, Types.INTEGER);

            if (book instanceof EBook) {
                EBook value = (EBook) book;
                statement.setString(8, value.getFormat());
                statement.setDouble(9, value.getFileSize());
            } else if (book instanceof PaperBook) {
                PaperBook value = (PaperBook) book;
                statement.setString(10, value.getPublisher());
                statement.setInt(11, value.getPrintRun());
            } else if (book instanceof AudioBook) {
                AudioBook value = (AudioBook) book;
                statement.setInt(12, value.getDurationMinutes());
                statement.setString(13, value.getNarrator());
            } else if (book instanceof TextBook) {
                TextBook value = (TextBook) book;
                statement.setString(14, value.getSubject());
                statement.setInt(15, value.getGradeLevel());
            }

            statement.executeUpdate();
        }
    }

    private String resolveType(Book book) {
        if (book.getClass() == Book.class) {
            return "Book";
        }
        if (book instanceof EBook) {
            return "EBook";
        }
        if (book instanceof PaperBook) {
            return "PaperBook";
        }
        if (book instanceof AudioBook) {
            return "AudioBook";
        }
        if (book instanceof TextBook) {
            return "TextBook";
        }
        throw new IllegalArgumentException("Unsupported book type: " + book.getClass().getName());
    }
}
