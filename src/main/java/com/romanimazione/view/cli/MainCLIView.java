package com.romanimazione.view.cli;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class MainCLIView {

    private final BufferedReader reader;
    private static final String P_CHOICE = "Choice: ";

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
        System.out.print(P_CHOICE);
        return reader.readLine();
    }

    public String showAdminMenuAndGetChoice(boolean isSuperAdmin) throws IOException {
        System.out.println("\n[ADMIN DASHBOARD]");
        System.out.println("1. Create New Party");
        System.out.println("2. List All Parties");
        if (isSuperAdmin) {
            System.out.println("3. Manage Users (Super Admin)");
            System.out.println("4. Change Master Code (Super Admin)");
            System.out.println("5. Manage Equipment (Magazzino)");
            System.out.println("6. Logout");
        } else {
            System.out.println("3. Manage Equipment (Magazzino)");
            System.out.println("4. Logout");
        }
        System.out.print(P_CHOICE);
        return reader.readLine();
    }

    public String showAnimatorMenuAndGetChoice() throws IOException {
        System.out.println("\n[ANIMATOR DASHBOARD]");
        System.out.println("1. Manage Availability");
        System.out.println("2. View Job Offers");
        System.out.println("3. View Accepted Jobs");
        System.out.println("4. Logout");
        System.out.print(P_CHOICE);
        return reader.readLine();
    }
    public String getNewMasterCode() throws IOException {
        System.out.println("\n--- CHANGE MASTER CODE ---");
        System.out.println("Warning: This code is required for future admin registrations.");
        System.out.print("Enter New Master Code (min 64 chars): ");
        return reader.readLine();
    }
}
