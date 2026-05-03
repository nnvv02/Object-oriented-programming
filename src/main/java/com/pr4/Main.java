package com.pr4;

import java.time.Year;
import java.util.List;
import java.util.Scanner;

public class Main {
    private static final Scanner SCANNER = new Scanner(System.in);

    public static void main(String[] args) {
        Library library = new Library();
        printHeader();

        while (true) {
            printMenu();
            int option = readInt("Choose option: ");

            switch (option) {
                case 1:
                    createBook(library);
                    break;
                case 2:
                    createBookCopy(library);
                    break;
                case 3:
                    printBooks(library);
                    break;
                case 4:
                    printCreatedBookCount();
                    break;
                case 5:
                    System.out.println("Exit.");
                    SCANNER.close();
                    return;
                default:
                    System.out.println("Invalid option. Enter 1 to 5.");
            }
        }
    }

    private static void printHeader() {
        System.out.println("Library Application - Practical Work #6");
        System.out.println("Static fields, copy constructor, enum, aggregation");
    }

    private static void printMenu() {
        System.out.println("\nMenu:");
        System.out.println("1. Create a new book");
        System.out.println("2. Copy an existing book");
        System.out.println("3. Show all books");
        System.out.println("4. Show total created books");
        System.out.println("5. Exit");
    }

    private static void createBook(Library library) {
        String title = readNonEmptyString("Title: ");
        String author = readNonEmptyString("Author: ");
        int year = readIntInRange("Year: ", 1, Year.now().getValue());
        int pages = readIntInRange("Pages: ", 1, Integer.MAX_VALUE);
        BookGenre genre = readGenre();

        try {
            Book book = new Book(title, author, year, pages, genre);
            library.addBook(book);
            System.out.println("Book created.");
            printCreatedBookCount();
        } catch (InvalidBookDataException e) {
            System.out.println("Invalid book data: " + e.getMessage());
        }
    }

    private static void createBookCopy(Library library) {
        if (library.getBookCount() == 0) {
            System.out.println("No books available to copy.");
            return;
        }

        List<Book> books = library.getBooks();
        System.out.println("Available books:");
        for (int i = 0; i < books.size(); i++) {
            System.out.println((i + 1) + ". " + books.get(i));
        }

        int index = readIntInRange("Enter book index to copy: ", 1, books.size());
        Book copy = new Book(books.get(index - 1));
        library.addBook(copy);
        System.out.println("Book copy created.");
        printCreatedBookCount();
    }

    private static void printBooks(Library library) {
        if (library.getBookCount() == 0) {
            System.out.println("No books available.");
            return;
        }

        System.out.println("\nBooks:");
        for (Book book : library.getBooks()) {
            System.out.println(book);
        }
    }

    private static void printCreatedBookCount() {
        System.out.println("Total created books: " + Book.getBookCount());
    }

    private static BookGenre readGenre() {
        BookGenre[] genres = BookGenre.values();
        System.out.println("Select genre:");
        for (int i = 0; i < genres.length; i++) {
            System.out.println((i + 1) + ". " + genres[i]);
        }

        int choice = readIntInRange("Genre number: ", 1, genres.length);
        return genres[choice - 1];
    }

    private static String readNonEmptyString(String prompt) {
        while (true) {
            System.out.print(prompt);
            String value = SCANNER.nextLine().trim();
            if (!value.isEmpty()) {
                return value;
            }
            System.out.println("Value must not be empty.");
        }
    }

    private static int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = SCANNER.nextLine().trim();
            try {
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Enter a valid integer.");
            }
        }
    }

    private static int readIntInRange(String prompt, int min, int max) {
        while (true) {
            int value = readInt(prompt);
            if (value >= min && value <= max) {
                return value;
            }
            System.out.println("Enter a value between " + min + " and " + max + ".");
        }
    }
}
