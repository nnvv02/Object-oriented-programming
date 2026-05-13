package com.pr4;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class Library {
    private final List<BookQuantity> inventory = new ArrayList<>();

    public void addBook(Book book) {
        addNewBook(book, 1);
    }

    public void addNewBook(Book bk, int quantity) {
        if (bk == null) {
            throw new InvalidBookDataException("Book must not be null");
        }
        if (quantity <= 0) {
            throw new InvalidBookDataException("Quantity must be positive");
        }

        BookQuantity existing = findInventoryEntry(bk);
        if (existing != null) {
            existing.addQuantity(quantity);
            return;
        }
        inventory.add(new BookQuantity(bk, quantity));
    }

    public void addAllBooks(List<Book> booksToAdd) {
        for (Book book : booksToAdd) {
            addNewBook(book, 1);
        }
    }

    public void clearBooks() {
        inventory.clear();
    }

    public List<Book> getBooks() {
        List<Book> books = new ArrayList<>();
        for (BookQuantity item : inventory) {
            books.add(item.getBook());
        }
        return Collections.unmodifiableList(books);
    }

    public List<BookQuantity> getInventory() {
        return Collections.unmodifiableList(inventory);
    }

    public boolean update(Book existingObject, Book newObject) {
        if (existingObject == null || newObject == null) {
            return false;
        }

        for (int i = 0; i < inventory.size(); i++) {
            BookQuantity item = inventory.get(i);
            if (!item.getBook().equals(existingObject)) {
                continue;
            }

            int quantity = item.getQuantity();
            inventory.remove(i);

            BookQuantity duplicate = findInventoryEntry(newObject);
            if (duplicate != null) {
                duplicate.addQuantity(quantity);
            } else {
                inventory.add(new BookQuantity(newObject, quantity));
            }
            return true;
        }

        return false;
    }

    public boolean delete(Book existingObject) {
        if (existingObject == null) {
            return false;
        }
        for (int i = 0; i < inventory.size(); i++) {
            if (inventory.get(i).getBook().equals(existingObject)) {
                inventory.remove(i);
                return true;
            }
        }
        return false;
    }

    public List<Book> getSortedBooks() {
        List<Book> books = new ArrayList<>(getBooks());
        Collections.sort(books);
        return books;
    }

    public int getBookCount() {
        return inventory.size();
    }

    public int getTotalQuantity() {
        int total = 0;
        for (BookQuantity item : inventory) {
            total += item.getQuantity();
        }
        return total;
    }

    public List<Book> findByAuthorContains(String query) {
        String normalized = query.toLowerCase(Locale.ROOT);
        List<Book> result = new ArrayList<>();
        for (BookQuantity item : inventory) {
            Book book = item.getBook();
            String author = book.getAuthor().toLowerCase(Locale.ROOT);
            if (author.contains(normalized)) {
                result.add(book);
            }
        }
        return result;
    }

    public List<Book> findByGenre(BookGenre genre) {
        List<Book> result = new ArrayList<>();
        for (BookQuantity item : inventory) {
            Book book = item.getBook();
            if (book.getGenre() == genre) {
                result.add(book);
            }
        }
        return result;
    }

    public List<Book> findByYearRange(int fromYear, int toYear) {
        List<Book> result = new ArrayList<>();
        for (BookQuantity item : inventory) {
            Book book = item.getBook();
            int year = book.getYear();
            if (year >= fromYear && year <= toYear) {
                result.add(book);
            }
        }
        return result;
    }

    public List<Book> findByType(int typeOption) {
        List<Book> result = new ArrayList<>();
        for (BookQuantity item : inventory) {
            Book book = item.getBook();
            if (matchesType(typeOption, book)) {
                result.add(book);
            }
        }
        return result;
    }

    public int getQuantity(Book book) {
        BookQuantity item = findInventoryEntry(book);
        if (item == null) {
            return 0;
        }
        return item.getQuantity();
    }

    public Book findByUuid(UUID uuid) {
        if (uuid == null) {
            return null;
        }
        for (BookQuantity item : inventory) {
            Book book = item.getBook();
            if (book.getUuid().equals(uuid)) {
                return book;
            }
        }
        return null;
    }

    public Book findByUuid(String uuidValue) {
        try {
            return findByUuid(UUID.fromString(uuidValue));
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    private BookQuantity findInventoryEntry(Book target) {
        for (BookQuantity item : inventory) {
            if (item.getBook().equals(target)) {
                return item;
            }
        }
        return null;
    }

    private boolean matchesType(int typeOption, Book book) {
        if (typeOption == 1) {
            return book instanceof GeneralBook;
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
