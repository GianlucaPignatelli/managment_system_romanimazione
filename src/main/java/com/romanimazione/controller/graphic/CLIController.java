package com.romanimazione.controller.graphic;

import com.romanimazione.bean.CredentialsBean;
import com.romanimazione.bean.UserBean;
import com.romanimazione.controller.application.LoginController;
import com.romanimazione.controller.application.Observer;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class CLIController implements Observer {

    private LoginController loginController;

    public CLIController() {
        this.loginController = new LoginController();
        this.loginController.attach(this);
    }

    public void start() {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {
            while (true) {
                System.out.println("\n--- MAIN MENU ---");
                System.out.println("1. Login");
                System.out.println("2. Register");
                System.out.println("3. Exit");
                System.out.print("Choice: ");
                String choice = reader.readLine();

                if ("1".equals(choice)) {
                    handleLogin(reader);
                } else if ("2".equals(choice)) {
                    handleRegister(reader);
                } else if ("3".equals(choice)) {
                    System.out.println("Goodbye!");
                    break;
                } else {
                    System.out.println("Invalid choice.");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleLogin(BufferedReader reader) throws Exception {
        System.out.print("Username: ");
        String username = reader.readLine();
        System.out.print("Password: ");
        String password = reader.readLine();

        CredentialsBean creds = new CredentialsBean(username, password);
        try {
            UserBean user = loginController.login(creds);
            System.out.println("Welcome " + user.getNome());
        } catch (Exception e) {
            System.out.println("Login Failed: " + e.getMessage());
        }
    }

    private void handleRegister(BufferedReader reader) throws Exception {
        System.out.println("\n--- REGISTER ---");
        // Registration Logic here - Need RegistrationController instance
        // For simplicity, let's assume we create it here or inject it.
        // In a real app, I'd move this to field.
        com.romanimazione.controller.application.RegistrationController regController = new com.romanimazione.controller.application.RegistrationController();
        regController.attach(this);

        System.out.print("Username: ");
        String username = reader.readLine();
        System.out.print("Password: ");
        String password = reader.readLine();
        System.out.print("Name: ");
        String nome = reader.readLine();
        System.out.print("Surname: ");
        String cognome = reader.readLine();
        System.out.print("Email (must end with @gmail.com): ");
        String email = reader.readLine();
        String roleInput = "";
        while (true) {
            System.out.print("Role (ANIMATORE/AMMINISTRATORE): ");
            roleInput = reader.readLine();
            if (com.romanimazione.entity.Role.fromString(roleInput) != null) {
                break;
            }
            System.out.println("Invalid Role. Please type 'ANIMATORE' or 'AMMINISTRATORE'.");
        }
        String role = com.romanimazione.entity.Role.fromString(roleInput).name();

        UserBean user = new UserBean();
        user.setUsername(username);
        user.setPassword(password);
        user.setNome(nome);
        user.setCognome(cognome);
        user.setEmail(email);
        user.setRole(role);

        try {
            regController.register(user);
            System.out.println("Registration successful! Please login.");
        } catch (Exception e) {
            System.out.println("Registration Failed: " + e.getMessage());
        }
    }

    @Override
    public void update(String message) {
        System.out.println("[NOTIFICATION]: " + message);
    }
}
