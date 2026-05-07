package com.pr4;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public final class BookStorage {
    private BookStorage() {
    }

    public static List<Book> loadFromText(Path path) {
        List<Book> books = new ArrayList<>();
        if (Files.notExists(path)) {
            return books;
        }

        try {
            List<String> lines = Files.readAllLines(path);
            for (String line : lines) {
                String trimmed = line.trim();
                if (trimmed.isEmpty()) {
                    continue;
                }
                books.add(parseLine(trimmed));
            }
        } catch (IOException e) {
            System.out.println("Text read error: " + e.getMessage());
        } catch (InvalidBookDataException e) {
            System.out.println("Text data error: " + e.getMessage());
        }

        return books;
    }

    public static void saveToText(Path path, List<Book> books) {
        List<String> lines = new ArrayList<>();
        for (Book book : books) {
            lines.add(serializeBook(book));
        }

        try {
            Files.write(path, lines);
        } catch (IOException e) {
            System.out.println("Text write error: " + e.getMessage());
        }
    }

    public static List<Book> loadFromJson(Path path) {
        List<Book> books = new ArrayList<>();
        if (Files.notExists(path)) {
            return books;
        }

        try {
            String content = Files.readString(path).trim();
            if (content.isEmpty() || "[]".equals(content)) {
                return books;
            }

            List<String> objects = splitJsonObjects(content);
            for (String object : objects) {
                Map<String, String> values = parseJsonObject(object);
                books.add(createBookFromJson(values));
            }
        } catch (IOException e) {
            System.out.println("JSON read error: " + e.getMessage());
        } catch (InvalidBookDataException e) {
            System.out.println("JSON data error: " + e.getMessage());
        }

        return books;
    }

    public static void saveToJson(Path path, List<Book> books) {
        StringBuilder builder = new StringBuilder();
        builder.append("[\n");
        for (int i = 0; i < books.size(); i++) {
            builder.append(toJsonObject(books.get(i)));
            if (i < books.size() - 1) {
                builder.append(",");
            }
            builder.append("\n");
        }
        builder.append("]\n");

        try {
            Files.writeString(path, builder.toString());
        } catch (IOException e) {
            System.out.println("JSON write error: " + e.getMessage());
        }
    }

    private static String serializeBook(Book book) {
        String type = book.getClass().getSimpleName();
        if (book instanceof GeneralBook) {
            type = "Book";
        }
        if (book instanceof EBook) {
            EBook value = (EBook) book;
            return String.join("|", type, esc(book.getTitle()), esc(book.getAuthor()),
                    Integer.toString(book.getYear()), Integer.toString(book.getPages()), book.getGenre().name(),
                    esc(value.getFormat()), Double.toString(value.getFileSize()));
        }
        if (book instanceof PaperBook) {
            PaperBook value = (PaperBook) book;
            return String.join("|", type, esc(book.getTitle()), esc(book.getAuthor()),
                    Integer.toString(book.getYear()), Integer.toString(book.getPages()), book.getGenre().name(),
                    esc(value.getPublisher()), Integer.toString(value.getPrintRun()));
        }
        if (book instanceof AudioBook) {
            AudioBook value = (AudioBook) book;
            return String.join("|", type, esc(book.getTitle()), esc(book.getAuthor()),
                    Integer.toString(book.getYear()), Integer.toString(book.getPages()), book.getGenre().name(),
                    Integer.toString(value.getDurationMinutes()), esc(value.getNarrator()));
        }
        if (book instanceof TextBook) {
            TextBook value = (TextBook) book;
            return String.join("|", type, esc(book.getTitle()), esc(book.getAuthor()),
                    Integer.toString(book.getYear()), Integer.toString(book.getPages()), book.getGenre().name(),
                    esc(value.getSubject()), Integer.toString(value.getGradeLevel()));
        }
        return String.join("|", type, esc(book.getTitle()), esc(book.getAuthor()),
                Integer.toString(book.getYear()), Integer.toString(book.getPages()), book.getGenre().name());
    }

    private static Book parseLine(String line) {
        String[] rawParts = line.split("\\|", -1);
        if (rawParts.length < 6) {
            throw new InvalidBookDataException("Invalid text record: " + line);
        }

        String[] parts = new String[rawParts.length];
        for (int i = 0; i < rawParts.length; i++) {
            parts[i] = unesc(rawParts[i]);
        }

        String type = parts[0];
        String title = parts[1];
        String author = parts[2];
        int year = parseInt(parts[3], "year");
        int pages = parseInt(parts[4], "pages");
        BookGenre genre = parseGenre(parts[5]);

        if ("Book".equals(type) || "GeneralBook".equals(type)) {
            ensureLength(parts, 6, type);
            return new GeneralBook(title, author, year, pages, genre);
        }
        if ("EBook".equals(type)) {
            ensureLength(parts, 8, type);
            String format = parts[6];
            double fileSize = parseDouble(parts[7], "fileSize");
            return new EBook(title, author, year, pages, genre, format, fileSize);
        }
        if ("PaperBook".equals(type)) {
            ensureLength(parts, 8, type);
            String publisher = parts[6];
            int printRun = parseInt(parts[7], "printRun");
            return new PaperBook(title, author, year, pages, genre, publisher, printRun);
        }
        if ("AudioBook".equals(type)) {
            ensureLength(parts, 8, type);
            int durationMinutes = parseInt(parts[6], "durationMinutes");
            String narrator = parts[7];
            return new AudioBook(title, author, year, pages, genre, durationMinutes, narrator);
        }
        if ("TextBook".equals(type)) {
            ensureLength(parts, 8, type);
            String subject = parts[6];
            int gradeLevel = parseInt(parts[7], "gradeLevel");
            return new TextBook(title, author, year, pages, genre, subject, gradeLevel);
        }

        throw new InvalidBookDataException("Unknown type: " + type);
    }

    private static String toJsonObject(Book book) {
        StringBuilder builder = new StringBuilder();
        builder.append("  {\n");
        appendJsonString(builder, "type", book.getClass().getSimpleName(), true);
        appendJsonString(builder, "title", book.getTitle(), true);
        appendJsonString(builder, "author", book.getAuthor(), true);
        appendJsonNumber(builder, "year", Integer.toString(book.getYear()), true);
        appendJsonNumber(builder, "pages", Integer.toString(book.getPages()), true);
        appendJsonString(builder, "genre", book.getGenre().name(), true);

        if (book instanceof EBook) {
            EBook value = (EBook) book;
            appendJsonString(builder, "format", value.getFormat(), true);
            appendJsonNumber(builder, "fileSize", Double.toString(value.getFileSize()), false);
        } else if (book instanceof PaperBook) {
            PaperBook value = (PaperBook) book;
            appendJsonString(builder, "publisher", value.getPublisher(), true);
            appendJsonNumber(builder, "printRun", Integer.toString(value.getPrintRun()), false);
        } else if (book instanceof AudioBook) {
            AudioBook value = (AudioBook) book;
            appendJsonNumber(builder, "durationMinutes", Integer.toString(value.getDurationMinutes()), true);
            appendJsonString(builder, "narrator", value.getNarrator(), false);
        } else if (book instanceof TextBook) {
            TextBook value = (TextBook) book;
            appendJsonString(builder, "subject", value.getSubject(), true);
            appendJsonNumber(builder, "gradeLevel", Integer.toString(value.getGradeLevel()), false);
        } else {
            trimTrailingComma(builder);
        }

        builder.append("\n  }");
        return builder.toString();
    }

    private static void appendJsonString(StringBuilder builder, String key, String value, boolean comma) {
        builder.append("    \"").append(jsonEscape(key)).append("\": \"").append(jsonEscape(value)).append("\"");
        if (comma) {
            builder.append(",");
        }
        builder.append("\n");
    }

    private static void appendJsonNumber(StringBuilder builder, String key, String value, boolean comma) {
        builder.append("    \"").append(jsonEscape(key)).append("\": ").append(value);
        if (comma) {
            builder.append(",");
        }
        builder.append("\n");
    }

    private static void trimTrailingComma(StringBuilder builder) {
        int index = builder.lastIndexOf(",\n");
        if (index >= 0) {
            builder.replace(index, index + 2, "\n");
        }
    }

    private static String jsonEscape(String value) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < value.length(); i++) {
            char current = value.charAt(i);
            if (current == '\\' || current == '"') {
                builder.append('\\');
            }
            builder.append(current);
        }
        return builder.toString();
    }

    private static List<String> splitJsonObjects(String content) {
        String trimmed = content.trim();
        if (!trimmed.startsWith("[") || !trimmed.endsWith("]")) {
            throw new InvalidBookDataException("Invalid JSON array");
        }

        String body = trimmed.substring(1, trimmed.length() - 1).trim();
        List<String> objects = new ArrayList<>();
        if (body.isEmpty()) {
            return objects;
        }

        int depth = 0;
        int start = -1;
        boolean inString = false;
        boolean escaped = false;

        for (int i = 0; i < body.length(); i++) {
            char current = body.charAt(i);
            if (escaped) {
                escaped = false;
                continue;
            }
            if (current == '\\') {
                escaped = true;
                continue;
            }
            if (current == '"') {
                inString = !inString;
                continue;
            }
            if (inString) {
                continue;
            }
            if (current == '{') {
                if (depth == 0) {
                    start = i;
                }
                depth++;
            } else if (current == '}') {
                depth--;
                if (depth == 0 && start >= 0) {
                    objects.add(body.substring(start, i + 1));
                }
            }
        }

        if (depth != 0) {
            throw new InvalidBookDataException("Invalid JSON object depth");
        }

        return objects;
    }

    private static Map<String, String> parseJsonObject(String object) {
        String trimmed = object.trim();
        if (!trimmed.startsWith("{") || !trimmed.endsWith("}")) {
            throw new InvalidBookDataException("Invalid JSON object");
        }

        String body = trimmed.substring(1, trimmed.length() - 1);
        List<String> pairs = splitPairs(body);
        Map<String, String> values = new HashMap<>();

        for (String pair : pairs) {
            String[] parts = splitKeyValue(pair);
            String key = parseJsonString(parts[0].trim());
            String value = parseJsonValue(parts[1].trim());
            values.put(key, value);
        }

        return values;
    }

    private static List<String> splitPairs(String body) {
        List<String> pairs = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean inString = false;
        boolean escaped = false;

        for (int i = 0; i < body.length(); i++) {
            char ch = body.charAt(i);
            if (escaped) {
                current.append(ch);
                escaped = false;
                continue;
            }
            if (ch == '\\') {
                current.append(ch);
                escaped = true;
                continue;
            }
            if (ch == '"') {
                inString = !inString;
                current.append(ch);
                continue;
            }
            if (ch == ',' && !inString) {
                String value = current.toString().trim();
                if (!value.isEmpty()) {
                    pairs.add(value);
                }
                current.setLength(0);
                continue;
            }
            current.append(ch);
        }

        String tail = current.toString().trim();
        if (!tail.isEmpty()) {
            pairs.add(tail);
        }
        return pairs;
    }

    private static String[] splitKeyValue(String pair) {
        boolean inString = false;
        boolean escaped = false;

        for (int i = 0; i < pair.length(); i++) {
            char ch = pair.charAt(i);
            if (escaped) {
                escaped = false;
                continue;
            }
            if (ch == '\\') {
                escaped = true;
                continue;
            }
            if (ch == '"') {
                inString = !inString;
                continue;
            }
            if (ch == ':' && !inString) {
                return new String[]{pair.substring(0, i), pair.substring(i + 1)};
            }
        }

        throw new InvalidBookDataException("Invalid key-value pair: " + pair);
    }

    private static String parseJsonValue(String value) {
        String trimmed = value.trim();
        if (trimmed.startsWith("\"") && trimmed.endsWith("\"")) {
            return parseJsonString(trimmed);
        }
        return trimmed;
    }

    private static String parseJsonString(String value) {
        String trimmed = value.trim();
        if (!trimmed.startsWith("\"") || !trimmed.endsWith("\"")) {
            throw new InvalidBookDataException("Invalid JSON string: " + value);
        }

        String body = trimmed.substring(1, trimmed.length() - 1);
        StringBuilder builder = new StringBuilder();
        boolean escaped = false;

        for (int i = 0; i < body.length(); i++) {
            char ch = body.charAt(i);
            if (escaped) {
                builder.append(ch);
                escaped = false;
            } else if (ch == '\\') {
                escaped = true;
            } else {
                builder.append(ch);
            }
        }

        if (escaped) {
            builder.append('\\');
        }

        return builder.toString();
    }

    private static Book createBookFromJson(Map<String, String> values) {
        String type = required(values, "type");
        String title = required(values, "title");
        String author = required(values, "author");
        int year = parseInt(required(values, "year"), "year");
        int pages = parseInt(required(values, "pages"), "pages");
        BookGenre genre = parseGenre(required(values, "genre"));

        if ("Book".equals(type) || "GeneralBook".equals(type)) {
            return new GeneralBook(title, author, year, pages, genre);
        }
        if ("EBook".equals(type)) {
            String format = required(values, "format");
            double fileSize = parseDouble(required(values, "fileSize"), "fileSize");
            return new EBook(title, author, year, pages, genre, format, fileSize);
        }
        if ("PaperBook".equals(type)) {
            String publisher = required(values, "publisher");
            int printRun = parseInt(required(values, "printRun"), "printRun");
            return new PaperBook(title, author, year, pages, genre, publisher, printRun);
        }
        if ("AudioBook".equals(type)) {
            int durationMinutes = parseInt(required(values, "durationMinutes"), "durationMinutes");
            String narrator = required(values, "narrator");
            return new AudioBook(title, author, year, pages, genre, durationMinutes, narrator);
        }
        if ("TextBook".equals(type)) {
            String subject = required(values, "subject");
            int gradeLevel = parseInt(required(values, "gradeLevel"), "gradeLevel");
            return new TextBook(title, author, year, pages, genre, subject, gradeLevel);
        }

        throw new InvalidBookDataException("Unknown type: " + type);
    }

    private static String required(Map<String, String> values, String key) {
        String value = values.get(key);
        if (value == null || value.isBlank()) {
            throw new InvalidBookDataException("Missing field: " + key);
        }
        return value;
    }

    private static void ensureLength(String[] parts, int expected, String type) {
        if (parts.length != expected) {
            throw new InvalidBookDataException("Invalid field count for " + type);
        }
    }

    private static int parseInt(String value, String name) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            throw new InvalidBookDataException("Invalid " + name + ": " + value);
        }
    }

    private static double parseDouble(String value, String name) {
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            throw new InvalidBookDataException("Invalid " + name + ": " + value);
        }
    }

    private static BookGenre parseGenre(String value) {
        try {
            return BookGenre.valueOf(value.toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException e) {
            throw new InvalidBookDataException("Invalid genre: " + value);
        }
    }

    private static String esc(String value) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < value.length(); i++) {
            char current = value.charAt(i);
            if (current == '\\' || current == '|') {
                builder.append('\\');
            }
            builder.append(current);
        }
        return builder.toString();
    }

    private static String unesc(String value) {
        StringBuilder builder = new StringBuilder();
        boolean escaped = false;
        for (int i = 0; i < value.length(); i++) {
            char current = value.charAt(i);
            if (escaped) {
                builder.append(current);
                escaped = false;
            } else if (current == '\\') {
                escaped = true;
            } else {
                builder.append(current);
            }
        }
        if (escaped) {
            builder.append('\\');
        }
        return builder.toString();
    }
}
