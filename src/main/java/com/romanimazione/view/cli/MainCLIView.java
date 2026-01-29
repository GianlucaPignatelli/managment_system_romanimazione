package com.romanimazione.view.cli;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class MainCLIView {

    private final BufferedReader reader;

    public MainCLIView() {
        this.reader = new BufferedReader(new InputStreamReader(System.in));
    }

    public void showWelcomeMessage() {
        System.out.println("Welcome to Romanimazione CLI");
    }

    public void showGoodbyeMessage() {
        System.out.println("Goodbye!");
    }

    public void showMessage(String message) {
        System.out.println(message);
    }

    public void showError(String message) {
        System.out.println("Error: " + message);
    }

    public String showMainMenuAndGetChoice() throws IOException {
        System.out.println("\n--- MAIN MENU ---");
        System.out.println("1. Login");
        System.out.println("2. Register");
        System.out.println("3. Exit");
        System.out.print("Choice: ");
        return reader.readLine();
    }

    public String showAdminMenuAndGetChoice() throws IOException {
        System.out.println("\n[ADMIN DASHBOARD]");
        System.out.println("1. Create New Party");
        System.out.println("2. List All Parties");
        System.out.println("3. Logout");
        System.out.print("Choice: ");
        return reader.readLine();
    }

    public String showAnimatorMenuAndGetChoice() throws IOException {
        System.out.println("\n[ANIMATORE DASHBOARD]");
        System.out.println("1. Manage Availability");
        System.out.println("2. Logout");
        System.out.print("Choice: ");
        return reader.readLine();
    }
}
