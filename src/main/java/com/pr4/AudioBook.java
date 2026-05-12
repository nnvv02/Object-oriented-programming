package com.pr4;

import java.util.Objects;

public class AudioBook extends Book {
    private int durationMinutes;
    private String narrator;

    public AudioBook(String title, String author, int year, int pages, int durationMinutes, String narrator) {
        this(title, author, year, pages, BookGenre.GENERAL, durationMinutes, narrator);
    }

    public AudioBook(String title, String author, int year, int pages, BookGenre genre, int durationMinutes, String narrator) {
        super(title, author, year, pages, genre);
        setDurationMinutes(durationMinutes);
        setNarrator(narrator);
    }

    public int getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(int durationMinutes) {
        if (durationMinutes <= 0) {
            throw new InvalidBookDataException("Duration must be positive");
        }
        this.durationMinutes = durationMinutes;
    }

    public String getNarrator() {
        return narrator;
    }

    public void setNarrator(String narrator) {
        if (narrator == null || narrator.isBlank()) {
            throw new InvalidBookDataException("Narrator must not be blank");
        }
        this.narrator = narrator;
    }

    @Override
    public String toString() {
        return "AudioBook{uuid=" + getUuid() + ", title='" + getTitle() + "', author='" + getAuthor() + "', year=" + getYear()
                + ", pages=" + getPages() + ", genre=" + getGenre() + ", durationMinutes=" + durationMinutes
                + ", narrator='" + narrator + "'}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        AudioBook audioBook = (AudioBook) o;
        return durationMinutes == audioBook.durationMinutes && Objects.equals(narrator, audioBook.narrator);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), durationMinutes, narrator);
    }
}
