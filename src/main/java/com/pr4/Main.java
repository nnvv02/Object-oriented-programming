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
                    createEBook(library);
                    break;
                case 3:
                    createPaperBook(library);
                    break;
                case 4:
                    createBookCopy(library);
                    break;
                case 5:
                    printBooks(library);
                    break;
                case 6:
                    printBooksByType(library);
                    break;
                case 7:
                    printCreatedBookCount();
                    break;
                case 8:
                    System.out.println("Exit.");
                    SCANNER.close();
                    return;
                default:
                    System.out.println("Invalid option. Enter 1 to 8.");
            }
        }
    }

    private static void printHeader() {
        System.out.println("Library Application - Practical Work #7");
        System.out.println("Polymorphism and ArrayList");
    }

    private static void printMenu() {
        System.out.println("\nMenu:");
        System.out.println("1. Create a new book");
        System.out.println("2. Create a new EBook");
        System.out.println("3. Create a new PaperBook");
        System.out.println("4. Copy an existing book");
        System.out.println("5. Show all books");
        System.out.println("6. Show books by type");
        System.out.println("7. Show total created books");
        System.out.println("8. Exit");
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

    private static void createEBook(Library library) {
        String title = readNonEmptyString("Title: ");
        String author = readNonEmptyString("Author: ");
        int year = readIntInRange("Year: ", 1, Year.now().getValue());
        int pages = readIntInRange("Pages: ", 1, Integer.MAX_VALUE);
        BookGenre genre = readGenre();
        String format = readNonEmptyString("Format (EPUB, PDF, MOBI): ");
        double fileSize = readDouble("File size (MB): ");

        try {
            EBook ebook = new EBook(title, author, year, pages, genre, format, fileSize);
            library.addBook(ebook);
            System.out.println("EBook created.");
            printCreatedBookCount();
        } catch (InvalidBookDataException e) {
            System.out.println("Invalid EBook data: " + e.getMessage());
        }
    }

    private static void createPaperBook(Library library) {
        String title = readNonEmptyString("Title: ");
        String author = readNonEmptyString("Author: ");
        int year = readIntInRange("Year: ", 1, Year.now().getValue());
        int pages = readIntInRange("Pages: ", 1, Integer.MAX_VALUE);
        BookGenre genre = readGenre();
        String publisher = readNonEmptyString("Publisher: ");
        int printRun = readIntInRange("Print run: ", 1, Integer.MAX_VALUE);

        try {
            PaperBook paperBook = new PaperBook(title, author, year, pages, genre, publisher, printRun);
            library.addBook(paperBook);
            System.out.println("PaperBook created.");
            printCreatedBookCount();
        } catch (InvalidBookDataException e) {
            System.out.println("Invalid PaperBook data: " + e.getMessage());
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
        Book originalBook = books.get(index - 1);
        Book copy;

        try {
            if (originalBook instanceof EBook) {
                copy = new EBook((EBook) originalBook);
            } else if (originalBook instanceof PaperBook) {
                copy = new PaperBook((PaperBook) originalBook);
            } else {
                copy = new Book(originalBook);
            }
            library.addBook(copy);
            System.out.println("Book copy created.");
            printCreatedBookCount();
        } catch (InvalidBookDataException e) {
            System.out.println("Invalid copy data: " + e.getMessage());
        }
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

    private static void printBooksByType(Library library) {
        library.printBooksByType();
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

    private static double readDouble(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = SCANNER.nextLine().trim();
            try {
                return Double.parseDouble(input);
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
