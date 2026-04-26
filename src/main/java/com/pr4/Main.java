package com.pr4;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Book[] library = new Book[5];
        Scanner scanner = new Scanner(System.in);

        System.out.println("Введіть дані для 5 книг:");

        for (int i = 0; i < library.length; i++) {
            System.out.println("Книга #" + (i + 1));
            System.out.print("Назва: ");
            String title = scanner.nextLine();
            System.out.print("Автор: ");
            String author = scanner.nextLine();
            int year = readYear(scanner);

            library[i] = new Book(title, author, year);
        }

        System.out.println("\nСписок створених книг:");
        for (Book book : library) {
            System.out.println(book.toString());
        }
        
        scanner.close();
    }

    private static int readYear(Scanner scanner) {
        while (true) {
            System.out.print("Рік: ");
            String input = scanner.nextLine();
            try {
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Некоректний рік. Введіть ціле число.");
            }
        }
    }
}
