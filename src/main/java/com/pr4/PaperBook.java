package com.pr4;

import java.util.Objects;

public class PaperBook extends Book {
    private String publisher;
    private int printRun;

    public PaperBook(String title, String author, int year, int pages, String publisher, int printRun) {
        this(title, author, year, pages, BookGenre.GENERAL, publisher, printRun);
    }

    public PaperBook(String title, String author, int year, int pages, BookGenre genre, String publisher, int printRun) {
        super(title, author, year, pages, genre);
        setPublisher(publisher);
        setPrintRun(printRun);
    }

    public PaperBook(PaperBook other) {
        super(other);
        this.publisher = other.publisher;
        this.printRun = other.printRun;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        if (publisher == null || publisher.isBlank()) {
            throw new InvalidBookDataException("Publisher must not be blank");
        }
        this.publisher = publisher;
    }

    public int getPrintRun() {
        return printRun;
    }

    public void setPrintRun(int printRun) {
        if (printRun <= 0) {
            throw new InvalidBookDataException("Print run must be positive");
        }
        this.printRun = printRun;
    }

    @Override
    public String toString() {
        return "PaperBook{title='" + getTitle() + "', author='" + getAuthor() + "', year=" + getYear() + 
               ", pages=" + getPages() + ", genre=" + getGenre() + ", publisher='" + publisher + "', printRun=" + printRun + "}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        PaperBook that = (PaperBook) o;
        return printRun == that.printRun && Objects.equals(publisher, that.publisher);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), publisher, printRun);
    }
}
