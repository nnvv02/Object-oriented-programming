package com.pr4;

import java.time.Year;
import java.util.Objects;

public class Book {
    private String title;
    private String author;
    private int year;
    private int pages;

    public Book(String title, String author, int year, int pages) {
        setTitle(title);
        setAuthor(author);
        setYear(year);
        setPages(pages);
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

    @Override
    public String toString() {
        return "Book{title='" + title + "', author='" + author + "', year=" + year + ", pages=" + pages + "}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Book book = (Book) o;
        return year == book.year && pages == book.pages && Objects.equals(title, book.title) && Objects.equals(author, book.author);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, author, year, pages);
    }
}