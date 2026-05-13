package com.pr4;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LibraryModificationEdgeTest {
    @Test
    void shouldMergeQuantitiesWhenUpdateTargetsExistingEquivalentBook() {
        Library library = new Library();
        Book dune = new GeneralBook("Dune", "Frank Herbert", 1965, 412, BookGenre.SCI_FI);
        Book hobbit = new GeneralBook("The Hobbit", "J.R.R. Tolkien", 1937, 310, BookGenre.FANTASY);
        library.addNewBook(dune, 2);
        library.addNewBook(hobbit, 3);

        boolean updated = library.update(dune, new GeneralBook("The Hobbit", "J.R.R. Tolkien", 1937, 310, BookGenre.FANTASY));

        assertTrue(updated);
        assertEquals(1, library.getBookCount());
        assertEquals(5, library.getTotalQuantity());
        assertEquals(5, library.getQuantity(hobbit));
    }

    @Test
    void shouldReturnFalseForNullArgumentsInUpdateAndDelete() {
        Library library = new Library();
        Book book = new GeneralBook("Book", "Author", 2020, 100, BookGenre.GENERAL);
        library.addBook(book);

        assertFalse(library.update(null, book));
        assertFalse(library.update(book, null));
        assertFalse(library.delete(null));
    }
}
