package com.pr4;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LibraryModificationTest {
    @Test
    void shouldUpdateExistingBook() {
        Library library = new Library();
        Book oldBook = new GeneralBook("Old", "Author", 2000, 100, BookGenre.GENERAL);
        Book newBook = new GeneralBook("New", "Author", 2001, 120, BookGenre.NON_FICTION);
        library.addNewBook(oldBook, 3);

        boolean updated = library.update(oldBook, newBook);

        assertTrue(updated);
        assertEquals(1, library.getBookCount());
        assertEquals(3, library.getTotalQuantity());
        assertEquals(0, library.getQuantity(oldBook));
        assertEquals(3, library.getQuantity(newBook));
    }

    @Test
    void shouldThrowObjectNotFoundWhenUpdatingMissingBook() {
        Library library = new Library();
        Book existing = new GeneralBook("A", "B", 2000, 100, BookGenre.GENERAL);
        Book replacement = new GeneralBook("C", "D", 2001, 120, BookGenre.FICTION);

        assertThrows(ObjectNotFoundException.class, () -> library.update(existing, replacement));
    }

    @Test
    void shouldDeleteExistingBook() {
        Library library = new Library();
        Book book = new GeneralBook("Delete", "Author", 2005, 210, BookGenre.FICTION);
        library.addNewBook(book, 2);

        boolean deleted = library.delete(book);

        assertTrue(deleted);
        assertEquals(0, library.getBookCount());
        assertEquals(0, library.getTotalQuantity());
    }

    @Test
    void shouldThrowObjectNotFoundWhenDeletingMissingBook() {
        Library library = new Library();
        Book missing = new GeneralBook("Missing", "Author", 2005, 210, BookGenre.FICTION);

        assertThrows(ObjectNotFoundException.class, () -> library.delete(missing));
    }
}
