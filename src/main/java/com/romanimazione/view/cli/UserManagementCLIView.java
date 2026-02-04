package com.romanimazione.view.cli;

import com.romanimazione.bean.UserBean;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

public class UserManagementCLIView {

    private final BufferedReader reader;

    public UserManagementCLIView() {
        this.reader = new BufferedReader(new InputStreamReader(System.in));
    }

    public void showUserList(List<UserBean> users) {
        System.out.println("\n--- USER MANAGEMENT ---");
        if (users.isEmpty()) {
            System.out.println("No users found.");
        } else {
            System.out.printf("%-5s | %-12s | %-15s | %-10s%n", "ID", "Username", "Name", "Role");
            System.out.println("---------------------------------------------------------");
            for (UserBean u : users) {
                String role = u.getRole();
                if (u.isSuperAdmin()) role += " (SUPER)";
                System.out.printf("%-5d | %-12s | %-15s | %-10s%n", 
                    u.getId(), u.getUsername(), u.getNome() + " " + u.getCognome(), role);
            }
        }
    }

    public int askUserIdToDelete() throws IOException {
        System.out.println("\nEnter User ID to delete (0 to go back): ");
        System.out.print("ID: ");
        try {
            return Integer.parseInt(reader.readLine());
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}
