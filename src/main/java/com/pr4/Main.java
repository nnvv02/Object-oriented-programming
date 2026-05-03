package com.pr4;

import java.time.Year;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {
    private static final Scanner SCANNER = new Scanner(System.in);

    public static void main(String[] args) {
        List<Book> library = new ArrayList<>();

        while (true) {
            printMenu();
            int option = readInt("Choose option: ");

            switch (option) {
                case 1:
                    createBook(library);
                    break;
                case 2:
                    printBooks(library);
                    break;
                case 3:
                    System.out.println("Exit.");
                    SCANNER.close();
                    return;
                default:
                    System.out.println("Invalid option. Enter 1, 2, or 3.");
            }
        }
    }

    private static void printMenu() {
        System.out.println("\nMenu:");
        System.out.println("1. Create a new book");
        System.out.println("2. Show all books");
        System.out.println("3. Exit");
    }

    private static void createBook(List<Book> library) {
        String title = readNonEmptyString("Title: ");
        String author = readNonEmptyString("Author: ");
        int year = readIntInRange("Year: ", 1, Year.now().getValue());
        int pages = readIntInRange("Pages: ", 1, Integer.MAX_VALUE);

        try {
            Book book = new Book(title, author, year, pages);
            library.add(book);
            System.out.println("Book created.");
        } catch (InvalidBookDataException e) {
            System.out.println("Invalid book data: " + e.getMessage());
        }
    }

    private static void printBooks(List<Book> library) {
        if (library.isEmpty()) {
            System.out.println("No books available.");
            return;
        }

        System.out.println("\nBooks:");
        for (Book book : library) {
            System.out.println(book);
        }
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
