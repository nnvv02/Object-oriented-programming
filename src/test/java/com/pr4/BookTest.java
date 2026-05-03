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
    }

    @Test
    void shouldThrowWhenInvalidConstructorData() {
        assertThrows(InvalidBookDataException.class, () -> new Book("", "Author", 2020, 120));
        assertThrows(InvalidBookDataException.class, () -> new Book("Title", null, 2020, 120));
        assertThrows(InvalidBookDataException.class, () -> new Book("Title", "Author", 3000, 120));
        assertThrows(InvalidBookDataException.class, () -> new Book("Title", "Author", 2020, 0));
    }

    @Test
    void shouldThrowWhenInvalidSetterValue() {
        Book book = new Book("Title", "Author", 2000, 150);

        assertThrows(InvalidBookDataException.class, () -> book.setYear(0));
        assertThrows(InvalidBookDataException.class, () -> book.setPages(-1));
    }
}