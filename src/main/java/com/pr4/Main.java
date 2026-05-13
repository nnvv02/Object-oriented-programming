package com.pr4;

import java.nio.file.Path;
import java.sql.SQLException;
import java.time.Year;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;

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
                    modifyBook(library);
                    break;
                case 4:
                    deleteBook(library);
                    break;
                case 5:
                    printBooks(library);
                    break;
                case 6:
                    printSortedBooks(library);
                    break;
                case 7:
                    saveBooks(library);
                    SCANNER.close();
                    return;
                default:
                    System.out.println("Invalid option. Enter 1 to 7.");
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
        System.out.println("3. Modify object");
        System.out.println("4. Delete object");
        System.out.println("5. Show all objects");
        System.out.println("6. Show all objects (sorted)");
        System.out.println("7. Exit");
    }

    private static void searchBooks(Library library) {
        while (true) {
            printSearchMenu();
            int option = readInt("Choose search option: ");
            if (option == 6) {
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
                case 5:
                    searchByUuid(library);
                    break;
                default:
                    System.out.println("Invalid search option. Enter 1 to 6.");
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
        System.out.println("5. By UUID");
        System.out.println("6. Back to main menu");
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

    private static void searchByUuid(Library library) {
        String uuidValue = readNonEmptyString("UUID: ");
        try {
            UUID uuid = UUID.fromString(uuidValue);
            Book found = library.findByUuid(uuid);
            if (found == null) {
                System.out.println("Object was not found.");
                return;
            }
            System.out.println("Found object:");
            System.out.println(found);
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid UUID format.");
        }
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
        if (books.isEmpty()) {
            System.out.println("Collection is empty.");
            return;
        }

        Comparator<Book> comparator = chooseSortComparator();
        if (comparator == null) {
            System.out.println("Sorting was cancelled.");
            return;
        }

        books.sort(comparator);
        System.out.println("Sorted objects:");
        for (Book book : books) {
            System.out.println(book);
        }
    }

    private static void printSortMenu() {
        System.out.println();
        System.out.println("Sorting criteria:");
        System.out.println("1. By title");
        System.out.println("2. By year");
        System.out.println("3. By pages");
        System.out.println("4. Back to main menu");
    }

    private static Comparator<Book> chooseSortComparator() {
        while (true) {
            printSortMenu();
            int option = readInt("Choose sorting option: ");
            switch (option) {
                case 1:
                    return (first, second) -> {
                        int byTitle = String.CASE_INSENSITIVE_ORDER.compare(first.getTitle(), second.getTitle());
                        if (byTitle != 0) {
                            return byTitle;
                        }
                        return String.CASE_INSENSITIVE_ORDER.compare(first.getAuthor(), second.getAuthor());
                    };
                case 2:
                    return (first, second) -> {
                        int byYear = Integer.compare(first.getYear(), second.getYear());
                        if (byYear != 0) {
                            return byYear;
                        }
                        return String.CASE_INSENSITIVE_ORDER.compare(first.getTitle(), second.getTitle());
                    };
                case 3:
                    return (first, second) -> {
                        int byPages = Integer.compare(first.getPages(), second.getPages());
                        if (byPages != 0) {
                            return byPages;
                        }
                        return String.CASE_INSENSITIVE_ORDER.compare(first.getTitle(), second.getTitle());
                    };
                case 4:
                    return null;
                default:
                    System.out.println("Invalid sorting option. Enter 1 to 4.");
            }
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

    private static void modifyBook(Library library) {
        if (library.getInventory().isEmpty()) {
            System.out.println("Collection is empty.");
            return;
        }

        printBooks(library);
        Book existing = readBookByUuid(library, "Enter UUID of object to modify: ");
        if (existing == null) {
            System.out.println("Object was not found.");
            return;
        }

        Book updated;
        try {
            updated = createUpdatedBook(existing);
        } catch (InvalidBookDataException e) {
            System.out.println("Invalid data: " + e.getMessage());
            return;
        }

        boolean updatedSuccessfully = library.update(existing, updated);
        if (updatedSuccessfully) {
            System.out.println("Object was modified.");
            return;
        }
        System.out.println("Object was not found.");
    }

    private static void deleteBook(Library library) {
        if (library.getInventory().isEmpty()) {
            System.out.println("Collection is empty.");
            return;
        }

        printBooks(library);
        Book existing = readBookByUuid(library, "Enter UUID of object to delete: ");
        if (existing == null) {
            System.out.println("Object was not found.");
            return;
        }

        if (!confirmDeletion()) {
            System.out.println("Deletion was cancelled.");
            return;
        }

        boolean deleted = library.delete(existing);
        if (deleted) {
            System.out.println("Object was deleted.");
            return;
        }
        System.out.println("Object was not found.");
    }

    private static Book readBookByUuid(Library library, String prompt) {
        String uuidValue = readNonEmptyString(prompt);
        Book found = library.findByUuid(uuidValue);
        if (found == null) {
            System.out.println("Invalid UUID format or object not found.");
            return null;
        }
        return found;
    }

    private static boolean confirmDeletion() {
        while (true) {
            String answer = readNonEmptyString("Confirm deletion (yes/no): ");
            if ("yes".equalsIgnoreCase(answer)) {
                return true;
            }
            if ("no".equalsIgnoreCase(answer)) {
                return false;
            }
            System.out.println("Enter yes or no.");
        }
    }

    private static Book createUpdatedBook(Book source) {
        if (source instanceof EBook) {
            return updateEBook((EBook) source);
        }
        if (source instanceof PaperBook) {
            return updatePaperBook((PaperBook) source);
        }
        if (source instanceof AudioBook) {
            return updateAudioBook((AudioBook) source);
        }
        if (source instanceof TextBook) {
            return updateTextBook((TextBook) source);
        }
        return updateGeneralBook((GeneralBook) source);
    }

    private static GeneralBook updateGeneralBook(GeneralBook source) {
        String title = source.getTitle();
        String author = source.getAuthor();
        int year = source.getYear();
        int pages = source.getPages();
        BookGenre genre = source.getGenre();

        printBaseAttributeMenu();
        int option = readIntInRange("Choose attribute: ", 1, 5);
        if (option == 1) {
            title = readNonEmptyString("New title: ");
        } else if (option == 2) {
            author = readNonEmptyString("New author: ");
        } else if (option == 3) {
            year = readIntInRange("New year: ", 1, CURRENT_YEAR);
        } else if (option == 4) {
            pages = readIntInRange("New pages: ", 1, Integer.MAX_VALUE);
        } else {
            genre = readGenre();
        }
        return new GeneralBook(title, author, year, pages, genre);
    }

    private static EBook updateEBook(EBook source) {
        String title = source.getTitle();
        String author = source.getAuthor();
        int year = source.getYear();
        int pages = source.getPages();
        BookGenre genre = source.getGenre();
        String format = source.getFormat();
        double fileSize = source.getFileSize();

        printEBookAttributeMenu();
        int option = readIntInRange("Choose attribute: ", 1, 7);
        if (option == 1) {
            title = readNonEmptyString("New title: ");
        } else if (option == 2) {
            author = readNonEmptyString("New author: ");
        } else if (option == 3) {
            year = readIntInRange("New year: ", 1, CURRENT_YEAR);
        } else if (option == 4) {
            pages = readIntInRange("New pages: ", 1, Integer.MAX_VALUE);
        } else if (option == 5) {
            genre = readGenre();
        } else if (option == 6) {
            format = readNonEmptyString("New format: ");
        } else {
            fileSize = readPositiveDouble("New file size (MB): ");
        }
        return new EBook(title, author, year, pages, genre, format, fileSize);
    }

    private static PaperBook updatePaperBook(PaperBook source) {
        String title = source.getTitle();
        String author = source.getAuthor();
        int year = source.getYear();
        int pages = source.getPages();
        BookGenre genre = source.getGenre();
        String publisher = source.getPublisher();
        int printRun = source.getPrintRun();

        printPaperBookAttributeMenu();
        int option = readIntInRange("Choose attribute: ", 1, 7);
        if (option == 1) {
            title = readNonEmptyString("New title: ");
        } else if (option == 2) {
            author = readNonEmptyString("New author: ");
        } else if (option == 3) {
            year = readIntInRange("New year: ", 1, CURRENT_YEAR);
        } else if (option == 4) {
            pages = readIntInRange("New pages: ", 1, Integer.MAX_VALUE);
        } else if (option == 5) {
            genre = readGenre();
        } else if (option == 6) {
            publisher = readNonEmptyString("New publisher: ");
        } else {
            printRun = readIntInRange("New print run: ", 1, Integer.MAX_VALUE);
        }
        return new PaperBook(title, author, year, pages, genre, publisher, printRun);
    }

    private static AudioBook updateAudioBook(AudioBook source) {
        String title = source.getTitle();
        String author = source.getAuthor();
        int year = source.getYear();
        int pages = source.getPages();
        BookGenre genre = source.getGenre();
        int durationMinutes = source.getDurationMinutes();
        String narrator = source.getNarrator();

        printAudioBookAttributeMenu();
        int option = readIntInRange("Choose attribute: ", 1, 7);
        if (option == 1) {
            title = readNonEmptyString("New title: ");
        } else if (option == 2) {
            author = readNonEmptyString("New author: ");
        } else if (option == 3) {
            year = readIntInRange("New year: ", 1, CURRENT_YEAR);
        } else if (option == 4) {
            pages = readIntInRange("New pages: ", 1, Integer.MAX_VALUE);
        } else if (option == 5) {
            genre = readGenre();
        } else if (option == 6) {
            durationMinutes = readIntInRange("New duration (minutes): ", 1, Integer.MAX_VALUE);
        } else {
            narrator = readNonEmptyString("New narrator: ");
        }
        return new AudioBook(title, author, year, pages, genre, durationMinutes, narrator);
    }

    private static TextBook updateTextBook(TextBook source) {
        String title = source.getTitle();
        String author = source.getAuthor();
        int year = source.getYear();
        int pages = source.getPages();
        BookGenre genre = source.getGenre();
        String subject = source.getSubject();
        int gradeLevel = source.getGradeLevel();

        printTextBookAttributeMenu();
        int option = readIntInRange("Choose attribute: ", 1, 7);
        if (option == 1) {
            title = readNonEmptyString("New title: ");
        } else if (option == 2) {
            author = readNonEmptyString("New author: ");
        } else if (option == 3) {
            year = readIntInRange("New year: ", 1, CURRENT_YEAR);
        } else if (option == 4) {
            pages = readIntInRange("New pages: ", 1, Integer.MAX_VALUE);
        } else if (option == 5) {
            genre = readGenre();
        } else if (option == 6) {
            subject = readNonEmptyString("New subject: ");
        } else {
            gradeLevel = readIntInRange("New grade level: ", 1, Integer.MAX_VALUE);
        }
        return new TextBook(title, author, year, pages, genre, subject, gradeLevel);
    }

    private static void printBaseAttributeMenu() {
        System.out.println("Attributes:");
        System.out.println("1. Title");
        System.out.println("2. Author");
        System.out.println("3. Year");
        System.out.println("4. Pages");
        System.out.println("5. Genre");
    }

    private static void printEBookAttributeMenu() {
        printBaseAttributeMenu();
        System.out.println("6. Format");
        System.out.println("7. File size");
    }

    private static void printPaperBookAttributeMenu() {
        printBaseAttributeMenu();
        System.out.println("6. Publisher");
        System.out.println("7. Print run");
    }

    private static void printAudioBookAttributeMenu() {
        printBaseAttributeMenu();
        System.out.println("6. Duration");
        System.out.println("7. Narrator");
    }

    private static void printTextBookAttributeMenu() {
        printBaseAttributeMenu();
        System.out.println("6. Subject");
        System.out.println("7. Grade level");
    }
}
