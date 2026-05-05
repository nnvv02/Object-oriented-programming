package com.pr4;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BookStorageTest {
    @Test
    void shouldSaveAndLoadText() throws IOException {
        Path tempFile = Files.createTempFile("books", ".txt");
        List<Book> source = createBooks();

        BookStorage.saveToText(tempFile, source);
        List<Book> loaded = BookStorage.loadFromText(tempFile);

        assertEquals(source.size(), loaded.size());
        assertEquals(source, loaded);
        assertInstanceOf(Book.class, loaded.get(0));
        assertInstanceOf(EBook.class, loaded.get(1));
        assertInstanceOf(PaperBook.class, loaded.get(2));
        assertInstanceOf(AudioBook.class, loaded.get(3));
        assertInstanceOf(TextBook.class, loaded.get(4));
    }

    @Test
    void shouldReturnEmptyWhenTextFileMissing() {
        Path path = Path.of("not-existing-books.txt");
        List<Book> loaded = BookStorage.loadFromText(path);
        assertTrue(loaded.isEmpty());
    }

    @Test
    void shouldSaveAndLoadJson() throws IOException {
        Path tempFile = Files.createTempFile("books", ".json");
        List<Book> source = createBooks();

        BookStorage.saveToJson(tempFile, source);
        List<Book> loaded = BookStorage.loadFromJson(tempFile);

        assertEquals(source.size(), loaded.size());
        assertEquals(source, loaded);
        assertInstanceOf(Book.class, loaded.get(0));
        assertInstanceOf(EBook.class, loaded.get(1));
        assertInstanceOf(PaperBook.class, loaded.get(2));
        assertInstanceOf(AudioBook.class, loaded.get(3));
        assertInstanceOf(TextBook.class, loaded.get(4));
    }

    @Test
    void shouldReturnEmptyWhenJsonFileMissing() {
        Path path = Path.of("not-existing-books.json");
        List<Book> loaded = BookStorage.loadFromJson(path);
        assertTrue(loaded.isEmpty());
    }

    private List<Book> createBooks() {
        List<Book> books = new ArrayList<>();
        books.add(new Book("Dune", "Frank Herbert", 1965, 412, BookGenre.SCI_FI));
        books.add(new EBook("Clean Code", "Robert C. Martin", 2008, 464, BookGenre.NON_FICTION, "PDF", 5.2));
        books.add(new PaperBook("The Hobbit", "J.R.R. Tolkien", 1937, 310, BookGenre.FANTASY, "Allen & Unwin", 10000));
        books.add(new AudioBook("1984", "George Orwell", 1949, 328, BookGenre.FICTION, 640, "Simon Prebble"));
        books.add(new TextBook("Math Basics", "A. Teacher", 2020, 220, BookGenre.GENERAL, "Mathematics", 8));
        return books;
    }
}
