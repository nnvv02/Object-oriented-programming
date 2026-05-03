package com.pr4;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Library {
    private final List<Book> books = new ArrayList<>();

    public void addBook(Book book) {
        books.add(book);
    }

    public List<Book> getBooks() {
        return Collections.unmodifiableList(books);
    }

    public int getBookCount() {
        return books.size();
    }

    public List<EBook> getEBooks() {
        List<EBook> ebooks = new ArrayList<>();
        for (Book book : books) {
            if (book instanceof EBook) {
                ebooks.add((EBook) book);
            }
        }
        return ebooks;
    }

    public List<PaperBook> getPaperBooks() {
        List<PaperBook> paperBooks = new ArrayList<>();
        for (Book book : books) {
            if (book instanceof PaperBook) {
                paperBooks.add((PaperBook) book);
            }
        }
        return paperBooks;
    }

    public void printBooksByType() {
        List<EBook> ebooks = getEBooks();
        List<PaperBook> paperBooks = getPaperBooks();

        if (ebooks.isEmpty() && paperBooks.isEmpty()) {
            System.out.println("No EBooks or PaperBooks available.");
            return;
        }

        if (!ebooks.isEmpty()) {
            System.out.println("\nEBooks:");
            for (EBook ebook : ebooks) {
                System.out.println(ebook);
            }
        }

        if (!paperBooks.isEmpty()) {
            System.out.println("\nPaperBooks:");
            for (PaperBook paperBook : paperBooks) {
                System.out.println(paperBook);
            }
        }
    }
}
