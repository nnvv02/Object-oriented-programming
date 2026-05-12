package com.pr4;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LibrarySearchTest {
    @Test
    void shouldFindByAuthorContainsIgnoringCase() {
        Library library = createLibrary();

        List<Book> result = library.findByAuthorContains("orWell");

        assertEquals(1, result.size());
        assertEquals("1984", result.get(0).getTitle());
    }

    @Test
    void shouldFindByGenre() {
        Library library = createLibrary();

        List<Book> result = library.findByGenre(BookGenre.SCI_FI);

        assertEquals(2, result.size());
    }

    @Test
    void shouldFindByYearRange() {
        Library library = createLibrary();

        List<Book> result = library.findByYearRange(2000, 2010);

        assertEquals(1, result.size());
        assertEquals("Clean Code", result.get(0).getTitle());
    }

    @Test
    void shouldFindByType() {
        Library library = createLibrary();

        List<Book> result = library.findByType(2);

        assertEquals(1, result.size());
        assertTrue(result.get(0) instanceof EBook);
    }

    @Test
    void shouldReturnEmptyWhenNoMatches() {
        Library library = createLibrary();

        List<Book> result = library.findByAuthorContains("Unknown");

        assertTrue(result.isEmpty());
    }

    @Test
    void shouldFindByUuid() {
        Library library = createLibrary();
        Book target = library.getBooks().get(0);

        Book found = library.findByUuid(target.getUuid());

        assertEquals(target, found);
    }

    @Test
    void shouldReturnNullForInvalidUuidString() {
        Library library = createLibrary();

        Book found = library.findByUuid("invalid-uuid");

        assertNull(found);
    }

    @Test
    void shouldTrackUniqueAndTotalCountsAfterAggregation() {
        Library library = createLibrary();

        library.addNewBook(new GeneralBook("Dune", "Frank Herbert", 1965, 412, BookGenre.SCI_FI), 4);

        assertEquals(5, library.getBookCount());
        assertEquals(9, library.getTotalQuantity());
    }

    private Library createLibrary() {
        Library library = new Library();
        library.addBook(new GeneralBook("Dune", "Frank Herbert", 1965, 412, BookGenre.SCI_FI));
        library.addBook(new EBook("Clean Code", "Robert C. Martin", 2008, 464, BookGenre.NON_FICTION, "PDF", 5.2));
        library.addBook(new PaperBook("The Hobbit", "J.R.R. Tolkien", 1937, 310, BookGenre.FANTASY, "Allen & Unwin", 10000));
        library.addBook(new AudioBook("1984", "George Orwell", 1949, 328, BookGenre.FICTION, 640, "Simon Prebble"));
        library.addBook(new TextBook("Physics 101", "A. Teacher", 2021, 220, BookGenre.SCI_FI, "Physics", 10));
        return library;
    }
}
