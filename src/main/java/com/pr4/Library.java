package com.pr4;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class Library {
    private final List<Book> books = new ArrayList<>();

    public void addBook(Book book) {
        books.add(book);
    }

    public void addAllBooks(List<Book> booksToAdd) {
        for (Book book : booksToAdd) {
            books.add(book);
        }
    }

    public void clearBooks() {
        books.clear();
    }

    public List<Book> getBooks() {
        return Collections.unmodifiableList(books);
    }

    public int getBookCount() {
        return books.size();
    }

    public List<Book> findByAuthorContains(String query) {
        String normalized = query.toLowerCase(Locale.ROOT);
        List<Book> result = new ArrayList<>();
        for (Book book : books) {
            String author = book.getAuthor().toLowerCase(Locale.ROOT);
            if (author.contains(normalized)) {
                result.add(book);
            }
        }
        return result;
    }

    public List<Book> findByGenre(BookGenre genre) {
        List<Book> result = new ArrayList<>();
        for (Book book : books) {
            if (book.getGenre() == genre) {
                result.add(book);
            }
        }
        return result;
    }

    public List<Book> findByYearRange(int fromYear, int toYear) {
        List<Book> result = new ArrayList<>();
        for (Book book : books) {
            int year = book.getYear();
            if (year >= fromYear && year <= toYear) {
                result.add(book);
            }
        }
        return result;
    }

    public List<Book> findByType(int typeOption) {
        List<Book> result = new ArrayList<>();
        for (Book book : books) {
            if (matchesType(typeOption, book)) {
                result.add(book);
            }
        }
        return result;
    }

    private boolean matchesType(int typeOption, Book book) {
        if (typeOption == 1) {
            return book.getClass() == Book.class;
        }
        if (typeOption == 2) {
            return book instanceof EBook;
        }
        if (typeOption == 3) {
            return book instanceof PaperBook;
        }
        if (typeOption == 4) {
            return book instanceof AudioBook;
        }
        if (typeOption == 5) {
            return book instanceof TextBook;
        }
        return false;
    }
}
