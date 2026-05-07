package com.pr4;

import java.nio.file.Path;
import java.sql.SQLException;
import java.time.Year;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

public class Main {
    private static final Scanner SCANNER = new Scanner(System.in);
    private static final int CURRENT_YEAR = Year.now().getValue();
    private static final Path TEXT_FILE = Path.of("input.txt");
    private static final Path JSON_FILE = Path.of("input.json");

    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Configuration file path is required. Usage: java Main db.properties");
            return;
        }

        BookRepository repository;
        try {
            DatabaseConfig config = DatabaseConfig.fromFile(Path.of(args[0]));
            repository = new BookRepository(config);
        } catch (IllegalArgumentException e) {
            System.out.println("Database configuration error: " + e.getMessage());
            return;
        }

        Library library = new Library();
        List<Book> loadedBooks = loadBooks();
        library.addAllBooks(loadedBooks);

        while (true) {
            printMenu();
            int option = readInt("Choose option: ");

            switch (option) {
                case 1:
                    searchBooks(library);
                    break;
                case 2:
                    createBookByType(library, repository);
                    break;
                case 3:
                    printBooks(library);
                    break;
                case 4:
                    printSortedBooks(library);
                    break;
                case 5:
                    saveBooks(library);
                    SCANNER.close();
                    return;
                default:
                    System.out.println("Invalid option. Enter 1 to 5.");
            }
        }
    }

    private static List<Book> loadBooks() {
        List<Book> booksFromText = BookStorage.loadFromText(TEXT_FILE);
        if (!booksFromText.isEmpty()) {
            return booksFromText;
        }

        List<Book> booksFromJson = BookStorage.loadFromJson(JSON_FILE);
        if (!booksFromJson.isEmpty()) {
            return booksFromJson;
        }

        return new ArrayList<>();
    }

    private static void saveBooks(Library library) {
        List<Book> books = new ArrayList<>();
        for (BookQuantity item : library.getInventory()) {
            for (int i = 0; i < item.getQuantity(); i++) {
                books.add(item.getBook());
            }
        }
        BookStorage.saveToText(TEXT_FILE, books);
        BookStorage.saveToJson(JSON_FILE, books);
    }

    private static void printMenu() {
        System.out.println();
        System.out.println("1. Search objects");
        System.out.println("2. Create a new object");
        System.out.println("3. Show all objects");
        System.out.println("4. Show all objects (sorted)");
        System.out.println("5. Exit");
    }

    private static void searchBooks(Library library) {
        while (true) {
            printSearchMenu();
            int option = readInt("Choose search option: ");
            if (option == 5) {
                return;
            }

            switch (option) {
                case 1:
                    searchByAuthor(library);
                    break;
                case 2:
                    searchByGenre(library);
                    break;
                case 3:
                    searchByYearRange(library);
                    break;
                case 4:
                    searchByType(library);
                    break;
                default:
                    System.out.println("Invalid search option. Enter 1 to 5.");
            }
        }
    }

    private static void printSearchMenu() {
        System.out.println();
        System.out.println("Search menu:");
        System.out.println("1. By author");
        System.out.println("2. By genre");
        System.out.println("3. By year range");
        System.out.println("4. By type");
        System.out.println("5. Back to main menu");
    }

    private static void searchByAuthor(Library library) {
        String authorQuery = readNonEmptyString("Author contains: ");
        List<Book> result = library.findByAuthorContains(authorQuery);
        printSearchResult(result);
    }

    private static void searchByGenre(Library library) {
        BookGenre genre = readGenre();
        List<Book> result = library.findByGenre(genre);
        printSearchResult(result);
    }

    private static void searchByYearRange(Library library) {
        int fromYear = readIntInRange("From year: ", 1, CURRENT_YEAR);
        int toYear = readIntInRange("To year: ", fromYear, CURRENT_YEAR);
        List<Book> result = library.findByYearRange(fromYear, toYear);
        printSearchResult(result);
    }

    private static void searchByType(Library library) {
        printTypeFilterMenu();
        int typeOption = readIntInRange("Type number: ", 1, 5);
        List<Book> result = library.findByType(typeOption);
        printSearchResult(result);
    }

    private static void printTypeFilterMenu() {
        System.out.println("Select type:");
        System.out.println("1. Book");
        System.out.println("2. EBook");
        System.out.println("3. PaperBook");
        System.out.println("4. AudioBook");
        System.out.println("5. TextBook");
    }

    private static void printSearchResult(List<Book> result) {
        if (result.isEmpty()) {
            System.out.println("No objects matched the search criteria.");
            return;
        }
        System.out.println("Found objects: " + result.size());
        for (Book book : result) {
            System.out.println(book);
        }
    }

    private static void createBookByType(Library library, BookRepository repository) {
        while (true) {
            printCreateMenu();
            int typeOption = readInt("Choose type: ");
            if (typeOption == 6) {
                return;
            }

            Book book = createBookInstance(typeOption);
            if (book != null) {
                int quantity = readIntInRange("Quantity: ", 1, Integer.MAX_VALUE);
                library.addNewBook(book, quantity);
                try {
                    repository.save(book, quantity);
                    System.out.println("Object created.");
                } catch (SQLException e) {
                    System.out.println("Database insert error: " + e.getMessage());
                }
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
                    return new GeneralBook(title, author, year, pages, genre);
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
        if (library.getInventory().isEmpty()) {
            System.out.println("Collection is empty.");
            return;
        }

        System.out.println("Unique objects: " + library.getBookCount());
        System.out.println("Total quantity: " + library.getTotalQuantity());
        for (BookQuantity item : library.getInventory()) {
            System.out.println(item.getBook() + ", quantity=" + item.getQuantity());
        }
    }

    private static void printSortedBooks(Library library) {
        List<Book> books = new ArrayList<>(library.getBooks());
        Collections.sort(books);

        if (books.isEmpty()) {
            System.out.println("Collection is empty.");
            return;
        }

        System.out.println("Sorted objects:");
        for (Book book : books) {
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
