package com.pr4;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BookTest {
    @Test
    void shouldCreateBookWithValidData() {
        Book book = new Book("Effective Java", "Joshua Bloch", 2018, 416);

        assertEquals("Effective Java", book.getTitle());
        assertEquals("Joshua Bloch", book.getAuthor());
        assertEquals(2018, book.getYear());
        assertEquals(416, book.getPages());
        assertEquals(BookGenre.GENERAL, book.getGenre());
    }

    @Test
    void shouldCreateBookWithEnumGenre() {
        int initialCount = Book.getBookCount();
        Book book = new Book("Dune", "Frank Herbert", 1965, 412, BookGenre.SCI_FI);

        assertEquals(BookGenre.SCI_FI, book.getGenre());
        assertEquals(initialCount + 1, Book.getBookCount());
    }

    @Test
    void shouldThrowWhenInvalidConstructorData() {
        assertThrows(InvalidBookDataException.class, () -> new Book("", "Author", 2020, 120));
        assertThrows(InvalidBookDataException.class, () -> new Book("Title", null, 2020, 120));
        assertThrows(InvalidBookDataException.class, () -> new Book("Title", "Author", 3000, 120));
        assertThrows(InvalidBookDataException.class, () -> new Book("Title", "Author", 2020, 0));
    }

    @Test
    void shouldUseCopyConstructor() {
        Book original = new Book("Title", "Author", 2000, 150, BookGenre.HISTORY);
        Book copy = new Book(original);

        assertEquals(original, copy);
        assertNotSame(original, copy);
    }

    @Test
    void shouldThrowWhenInvalidSetterValue() {
        Book book = new Book("Title", "Author", 2000, 150);

        assertThrows(InvalidBookDataException.class, () -> book.setYear(0));
        assertThrows(InvalidBookDataException.class, () -> book.setPages(-1));
    }
}
