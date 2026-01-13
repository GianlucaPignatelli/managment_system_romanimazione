package com.romanimazione.view;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        System.out.println("Select Interface:");
        System.out.println("1. Command Line Interface (CLI)");
        System.out.println("2. Graphical User Interface (JavaFX)");
        System.out.print("Choice: ");

        Scanner scanner = new Scanner(System.in);
        String choice = scanner.nextLine();

        if ("1".equals(choice)) {
            MainCLI.main(args);
        } else if ("2".equals(choice)) {
            MainApp.main(args);
        } else {
            System.out.println("Invalid choice. Defaulting to CLI.");
            MainCLI.main(args);
        }
    }
}
