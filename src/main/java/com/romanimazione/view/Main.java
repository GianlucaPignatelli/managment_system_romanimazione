package com.romanimazione.view;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        
        System.out.println("Select Persistence Mode:");
        System.out.println("1. MySQL");
        System.out.println("2. File System (JSON)");
        System.out.println("3. Demo (In-Memory)");
        System.out.print("Choice: ");
        String pChoice = scanner.nextLine();
        
        int pType = com.romanimazione.dao.DAOFactory.DEMO;
        if ("1".equals(pChoice)) pType = com.romanimazione.dao.DAOFactory.MYSQL;
        else if ("2".equals(pChoice)) pType = com.romanimazione.dao.DAOFactory.FILESYSTEM;
        
        com.romanimazione.dao.DAOFactory.setFactoryType(pType);
        String pTypeName = "Demo";
        if (pType == com.romanimazione.dao.DAOFactory.MYSQL) pTypeName = "MySQL";
        else if (pType == com.romanimazione.dao.DAOFactory.FILESYSTEM) pTypeName = "File";
        
        System.out.println("Persistence set to: " + pTypeName);

        System.out.println("\nSelect Interface:");
        System.out.println("1. Command Line Interface (CLI)");
        System.out.println("2. Graphical User Interface (JavaFX)");
        System.out.print("Choice: ");

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
