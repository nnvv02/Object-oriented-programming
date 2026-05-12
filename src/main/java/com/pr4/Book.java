package com.pr4;

import java.time.Year;
import java.util.Objects;
import java.util.UUID;

public abstract class Book implements Comparable<Book>, Identifiable {
    private static int bookCount;
    private final UUID uuid;
    private String title;
    private String author;
    private int year;
    private int pages;
    private BookGenre genre;

    public Book(String title, String author, int year, int pages) {
        this(title, author, year, pages, BookGenre.GENERAL);
    }

    public Book(String title, String author, int year, int pages, BookGenre genre) {
        this.uuid = UUID.randomUUID();
        setTitle(title);
        setAuthor(author);
        setYear(year);
        setPages(pages);
        setGenre(genre);
        bookCount++;
    }

    public Book(Book other) {
        this(other.title, other.author, other.year, other.pages, other.genre);
    }

    @Override
    public UUID getUuid() {
        return uuid;
    }

    public static int getBookCount() {
        return bookCount;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        if (title == null || title.isBlank()) {
            throw new InvalidBookDataException("Title must not be blank");
        }
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        if (author == null || author.isBlank()) {
            throw new InvalidBookDataException("Author must not be blank");
        }
        this.author = author;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        int currentYear = Year.now().getValue();
        if (year <= 0 || year > currentYear) {
            throw new InvalidBookDataException("Year must be between 1 and " + currentYear);
        }
        this.year = year;
    }

    public int getPages() {
        return pages;
    }

    public void setPages(int pages) {
        if (pages <= 0) {
            throw new InvalidBookDataException("Pages must be positive");
        }
        this.pages = pages;
    }

    public BookGenre getGenre() {
        return genre;
    }

    public void setGenre(BookGenre genre) {
        if (genre == null) {
            throw new InvalidBookDataException("Genre must not be null");
        }
        this.genre = genre;
    }

    @Override
    public String toString() {
        return "Book{uuid=" + uuid + ", title='" + title + "', author='" + author + "', year=" + year + ", pages=" + pages + ", genre=" + genre + "}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Book book = (Book) o;
        return year == book.year && pages == book.pages && Objects.equals(title, book.title) && Objects.equals(author, book.author) && genre == book.genre;
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, author, year, pages, genre);
    }

    @Override
    public int compareTo(Book other) {
        if (other == null) {
            return 1;
        }

        int byTitle = String.CASE_INSENSITIVE_ORDER.compare(title, other.title);
        if (byTitle != 0) {
            return byTitle;
        }

        int byAuthor = String.CASE_INSENSITIVE_ORDER.compare(author, other.author);
        if (byAuthor != 0) {
            return byAuthor;
        }

        int byYear = Integer.compare(year, other.year);
        if (byYear != 0) {
            return byYear;
        }

        return Integer.compare(pages, other.pages);
    }
}
