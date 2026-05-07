package com.pr4;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BookSortingTest {
    @Test
    void shouldSortEmptyList() {
        List<Book> books = new ArrayList<>();

        Collections.sort(books);

        assertTrue(books.isEmpty());
    }

    @Test
    void shouldKeepSingleElementList() {
        List<Book> books = new ArrayList<>();
        books.add(new GeneralBook("Dune", "Frank Herbert", 1965, 412, BookGenre.SCI_FI));

        Collections.sort(books);

        assertEquals(1, books.size());
        assertEquals("Dune", books.get(0).getTitle());
    }

    @Test
    void shouldSortMultipleBooksByTitle() {
        List<Book> books = new ArrayList<>();
        books.add(new EBook("Zeta", "Author Z", 2012, 150, BookGenre.NON_FICTION, "PDF", 2.0));
        books.add(new GeneralBook("Alpha", "Author A", 2010, 100, BookGenre.FICTION));
        books.add(new PaperBook("Gamma", "Author G", 2011, 200, BookGenre.FANTASY, "Publisher", 1000));

        Collections.sort(books);

        assertEquals("Alpha", books.get(0).getTitle());
        assertEquals("Gamma", books.get(1).getTitle());
        assertEquals("Zeta", books.get(2).getTitle());
    }

    @Test
    void shouldReturnSortedBooksFromLibrary() {
        Library library = new Library();
        library.addBook(new GeneralBook("Beta", "Author B", 2010, 100, BookGenre.FICTION));
        library.addBook(new GeneralBook("Alpha", "Author A", 2009, 150, BookGenre.FICTION));

        List<Book> sorted = library.getSortedBooks();

        assertEquals(2, sorted.size());
        assertEquals("Alpha", sorted.get(0).getTitle());
        assertEquals("Beta", sorted.get(1).getTitle());
    }
}
