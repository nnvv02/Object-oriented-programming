package com.pr4;

import java.util.Objects;

public class TextBook extends Book {
    private String subject;
    private int gradeLevel;

    public TextBook(String title, String author, int year, int pages, String subject, int gradeLevel) {
        this(title, author, year, pages, BookGenre.GENERAL, subject, gradeLevel);
    }

    public TextBook(String title, String author, int year, int pages, BookGenre genre, String subject, int gradeLevel) {
        super(title, author, year, pages, genre);
        setSubject(subject);
        setGradeLevel(gradeLevel);
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        if (subject == null || subject.isBlank()) {
            throw new InvalidBookDataException("Subject must not be blank");
        }
        this.subject = subject;
    }

    public int getGradeLevel() {
        return gradeLevel;
    }

    public void setGradeLevel(int gradeLevel) {
        if (gradeLevel <= 0) {
            throw new InvalidBookDataException("Grade level must be positive");
        }
        this.gradeLevel = gradeLevel;
    }

    @Override
    public String toString() {
        return "TextBook{uuid=" + getUuid() + ", title='" + getTitle() + "', author='" + getAuthor() + "', year=" + getYear()
                + ", pages=" + getPages() + ", genre=" + getGenre() + ", subject='" + subject
                + "', gradeLevel=" + gradeLevel + "}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        TextBook textBook = (TextBook) o;
        return gradeLevel == textBook.gradeLevel && Objects.equals(subject, textBook.subject);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), subject, gradeLevel);
    }
}
