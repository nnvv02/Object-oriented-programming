package com.pr4;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
}
