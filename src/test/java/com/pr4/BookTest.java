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

    @Test
    void shouldCreateEBookWithValidData() {
        EBook ebook = new EBook("Test EBook", "Author", 2020, 300, BookGenre.SCI_FI, "EPUB", 2.5);

        assertEquals("Test EBook", ebook.getTitle());
        assertEquals("EPUB", ebook.getFormat());
        assertEquals(2.5, ebook.getFileSize());
        assertEquals(BookGenre.SCI_FI, ebook.getGenre());
    }

    @Test
    void shouldThrowWhenInvalidEBookData() {
        assertThrows(InvalidBookDataException.class, () -> new EBook("Title", "Author", 2020, 300, "", 2.5));
        assertThrows(InvalidBookDataException.class, () -> new EBook("Title", "Author", 2020, 300, "PDF", -1));
    }

    @Test
    void shouldCreateEBookCopy() {
        EBook original = new EBook("Test EBook", "Author", 2020, 300, BookGenre.FICTION, "PDF", 3.0);
        EBook copy = new EBook(original);

        assertEquals(original, copy);
        assertNotSame(original, copy);
    }

    @Test
    void shouldCreatePaperBookWithValidData() {
        PaperBook paperBook = new PaperBook("Test Book", "Author", 2020, 350, BookGenre.FANTASY, "Publisher Co", 5000);

        assertEquals("Test Book", paperBook.getTitle());
        assertEquals("Publisher Co", paperBook.getPublisher());
        assertEquals(5000, paperBook.getPrintRun());
        assertEquals(BookGenre.FANTASY, paperBook.getGenre());
    }

    @Test
    void shouldThrowWhenInvalidPaperBookData() {
        assertThrows(InvalidBookDataException.class, () -> new PaperBook("Title", "Author", 2020, 300, "", 1000));
        assertThrows(InvalidBookDataException.class, () -> new PaperBook("Title", "Author", 2020, 300, "Publisher", 0));
    }

    @Test
    void shouldCreatePaperBookCopy() {
        PaperBook original = new PaperBook("Test Book", "Author", 2020, 350, BookGenre.MYSTERY, "Publisher", 1000);
        PaperBook copy = new PaperBook(original);

        assertEquals(original, copy);
        assertNotSame(original, copy);
    }
}

