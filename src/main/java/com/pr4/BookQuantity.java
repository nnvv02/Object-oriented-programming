package com.pr4;

public class BookQuantity {
    private final Book book;
    private int quantity;

    public BookQuantity(Book book, int quantity) {
        if (book == null) {
            throw new InvalidBookDataException("Book must not be null");
        }
        if (quantity <= 0) {
            throw new InvalidBookDataException("Quantity must be positive");
        }
        this.book = book;
        this.quantity = quantity;
    }

    public Book getBook() {
        return book;
    }

    public int getQuantity() {
        return quantity;
    }

    public void addQuantity(int amount) {
        if (amount <= 0) {
            throw new InvalidBookDataException("Quantity increment must be positive");
        }
        quantity += amount;
    }
}
