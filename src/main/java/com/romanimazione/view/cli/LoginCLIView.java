package com.romanimazione.view.cli;

import com.romanimazione.bean.CredentialsBean;
import com.romanimazione.bean.UserBean;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class LoginCLIView {

    private final BufferedReader reader;

    public LoginCLIView() {
        this.reader = new BufferedReader(new InputStreamReader(System.in));
    }

    public CredentialsBean getLoginCredentials() throws IOException {
        System.out.print("Username: ");
        String username = reader.readLine();
        System.out.print("Password: ");
        String password = reader.readLine();
        return new CredentialsBean(username, password);
    }

    public UserBean getRegistrationDetails(boolean isFirstAdmin) throws IOException {
        System.out.println("\n--- REGISTER ---");
        System.out.print("Username: ");
        String username = reader.readLine();
        System.out.print("Password: ");
        String password = reader.readLine();
        System.out.print("Name: ");
        String name = reader.readLine();
        System.out.print("Surname: ");
        String surname = reader.readLine();
        System.out.print("Email (must differ @gmail.com): ");
        String email = reader.readLine();
        
        String role = null;
        while (role == null) {
            System.out.println("Role (1. ANIMATORE, 2. ADMIN): ");
            String input = reader.readLine();
            if ("1".equals(input)) {
                role = "ANIMATORE";
            } else if ("2".equals(input)) {
                role = "AMMINISTRATORE";
            } else {
                System.out.println("Invalid choice. Please enter 1 or 2.");
            }
        }
        
        String securityCode = null;
        if ("AMMINISTRATORE".equals(role)) {
            String prompt = isFirstAdmin ? "Create NEW Master Code (min 64 chars): " : "Enter Master Code: ";
            System.out.print(prompt);
            securityCode = reader.readLine();
        }

        UserBean userBean = new UserBean();
        userBean.setUsername(username);
        userBean.setPassword(password);
        userBean.setNome(name);
        userBean.setCognome(surname);
        userBean.setEmail(email);
        userBean.setRole(role);
        userBean.setSecurityCode(securityCode);
        return userBean;
    }
}
