package com.pr4;

public class GeneralBook extends Book {
    public GeneralBook(String title, String author, int year, int pages) {
        super(title, author, year, pages);
    }

    public GeneralBook(String title, String author, int year, int pages, BookGenre genre) {
        super(title, author, year, pages, genre);
    }

    public GeneralBook(GeneralBook other) {
        super(other);
    }

    @Override
    public String toString() {
        return "Book{uuid=" + getUuid() + ", title='" + getTitle() + "', author='" + getAuthor() + "', year=" + getYear()
                + ", pages=" + getPages() + ", genre=" + getGenre() + "}";
    }
}
