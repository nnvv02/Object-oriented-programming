package com.pr4;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class LibraryAggregationTest {
    @Test
    void shouldIncreaseQuantityWhenBookAlreadyExists() {
        Library library = new Library();
        Book book = new Book("Dune", "Frank Herbert", 1965, 412, BookGenre.SCI_FI);

        library.addNewBook(book, 2);
        library.addNewBook(new Book("Dune", "Frank Herbert", 1965, 412, BookGenre.SCI_FI), 3);

        assertEquals(1, library.getBookCount());
        assertEquals(5, library.getTotalQuantity());
        assertEquals(5, library.getQuantity(book));
    }

    @Test
    void shouldAggregateDuplicatesFromLoadedList() {
        Library library = new Library();
        List<Book> loaded = new ArrayList<>();
        loaded.add(new Book("1984", "George Orwell", 1949, 328, BookGenre.FICTION));
        loaded.add(new Book("1984", "George Orwell", 1949, 328, BookGenre.FICTION));
        loaded.add(new Book("Clean Code", "Robert C. Martin", 2008, 464, BookGenre.NON_FICTION));

        library.addAllBooks(loaded);

        assertEquals(2, library.getBookCount());
        assertEquals(3, library.getTotalQuantity());
        assertEquals(2, library.getQuantity(new Book("1984", "George Orwell", 1949, 328, BookGenre.FICTION)));
    }

    @Test
    void shouldRejectInvalidQuantity() {
        Library library = new Library();
        Book book = new Book("Book", "Author", 2020, 100, BookGenre.GENERAL);

        assertThrows(InvalidBookDataException.class, () -> library.addNewBook(book, 0));
        assertThrows(InvalidBookDataException.class, () -> library.addNewBook(book, -1));
    }
}
