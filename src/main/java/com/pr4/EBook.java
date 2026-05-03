package com.pr4;

import java.util.Objects;

public class EBook extends Book {
    private String format;
    private double fileSize;

    public EBook(String title, String author, int year, int pages, String format, double fileSize) {
        this(title, author, year, pages, BookGenre.GENERAL, format, fileSize);
    }

    public EBook(String title, String author, int year, int pages, BookGenre genre, String format, double fileSize) {
        super(title, author, year, pages, genre);
        setFormat(format);
        setFileSize(fileSize);
    }

    public EBook(EBook other) {
        super(other);
        this.format = other.format;
        this.fileSize = other.fileSize;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        if (format == null || format.isBlank()) {
            throw new InvalidBookDataException("Format must not be blank");
        }
        this.format = format;
    }

    public double getFileSize() {
        return fileSize;
    }

    public void setFileSize(double fileSize) {
        if (fileSize <= 0) {
            throw new InvalidBookDataException("File size must be positive");
        }
        this.fileSize = fileSize;
    }

    @Override
    public String toString() {
        return "EBook{title='" + getTitle() + "', author='" + getAuthor() + "', year=" + getYear() + 
               ", pages=" + getPages() + ", genre=" + getGenre() + ", format='" + format + "', fileSize=" + fileSize + " MB}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        EBook ebook = (EBook) o;
        return Double.compare(ebook.fileSize, fileSize) == 0 && Objects.equals(format, ebook.format);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), format, fileSize);
    }
}
