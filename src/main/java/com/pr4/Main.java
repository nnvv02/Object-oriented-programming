package com.pr4;

import java.time.Year;
import java.util.Scanner;

public class Main {
    private static final Scanner SCANNER = new Scanner(System.in);
    private static final int CURRENT_YEAR = Year.now().getValue();

    public static void main(String[] args) {
        Library library = new Library();

        while (true) {
            printMenu();
            int option = readInt("Choose option: ");

            switch (option) {
                case 1:
                    createBookByType(library);
                    break;
                case 2:
                    printBooks(library);
                    break;
                case 3:
                    SCANNER.close();
                    return;
                default:
                    System.out.println("Invalid option. Enter 1 to 3.");
            }
        }
    }

    private static void printMenu() {
        System.out.println();
        System.out.println("1. Create a new object");
        System.out.println("2. Show all objects");
        System.out.println("3. Exit");
    }

    private static void createBookByType(Library library) {
        while (true) {
            printCreateMenu();
            int typeOption = readInt("Choose type: ");
            if (typeOption == 6) {
                return;
            }

            Book book = createBookInstance(typeOption);
            if (book != null) {
                library.addBook(book);
                System.out.println("Object created.");
                return;
            }
        }
    }

    private static void printCreateMenu() {
        System.out.println();
        System.out.println("1. Book");
        System.out.println("2. EBook");
        System.out.println("3. PaperBook");
        System.out.println("4. AudioBook");
        System.out.println("5. TextBook");
        System.out.println("6. Back to main menu");
    }

    private static Book createBookInstance(int typeOption) {
        if (typeOption < 1 || typeOption > 5) {
            System.out.println("Invalid type option. Enter 1 to 6.");
            return null;
        }

        String title = readNonEmptyString("Title: ");
        String author = readNonEmptyString("Author: ");
        int year = readIntInRange("Year: ", 1, CURRENT_YEAR);
        int pages = readIntInRange("Pages: ", 1, Integer.MAX_VALUE);
        BookGenre genre = readGenre();

        try {
            switch (typeOption) {
                case 1:
                    return new Book(title, author, year, pages, genre);
                case 2:
                    String format = readNonEmptyString("Format: ");
                    double fileSize = readPositiveDouble("File size (MB): ");
                    return new EBook(title, author, year, pages, genre, format, fileSize);
                case 3:
                    String publisher = readNonEmptyString("Publisher: ");
                    int printRun = readIntInRange("Print run: ", 1, Integer.MAX_VALUE);
                    return new PaperBook(title, author, year, pages, genre, publisher, printRun);
                case 4:
                    int durationMinutes = readIntInRange("Duration (minutes): ", 1, Integer.MAX_VALUE);
                    String narrator = readNonEmptyString("Narrator: ");
                    return new AudioBook(title, author, year, pages, genre, durationMinutes, narrator);
                default:
                    String subject = readNonEmptyString("Subject: ");
                    int gradeLevel = readIntInRange("Grade level: ", 1, Integer.MAX_VALUE);
                    return new TextBook(title, author, year, pages, genre, subject, gradeLevel);
            }
        } catch (InvalidBookDataException e) {
            System.out.println("Invalid data: " + e.getMessage());
            return null;
        }
    }

    private static void printBooks(Library library) {
        if (library.getBooks().isEmpty()) {
            System.out.println("Collection is empty.");
            return;
        }

        for (Book book : library.getBooks()) {
            System.out.println(book);
        }
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

    private static double readPositiveDouble(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = SCANNER.nextLine().trim();
            try {
                double value = Double.parseDouble(input);
                if (value > 0) {
                    return value;
                }
                System.out.println("Enter a value greater than 0.");
            } catch (NumberFormatException e) {
                System.out.println("Enter a valid number.");
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
