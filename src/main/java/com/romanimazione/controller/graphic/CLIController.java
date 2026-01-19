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
            System.out.println("System Error: " + e.getMessage());
        }
    }

    private void handleLogin(BufferedReader reader) throws java.io.IOException {
        System.out.print("Username: ");
        String username = reader.readLine();
        System.out.print("Password: ");
        String password = reader.readLine();

        CredentialsBean creds = new CredentialsBean(username, password);
        try {
            UserBean user = loginController.login(creds);
            com.romanimazione.bean.SessionBean.getInstance().setCurrentUser(user);
            System.out.println("Login Successful! Welcome " + user.getNome());
            
            // Route based on role
            if ("ANIMATORE".equalsIgnoreCase(user.getRole())) {
                runAnimatorSession(reader);
            } else if ("AMMINISTRATORE".equalsIgnoreCase(user.getRole())) {
                runAdminSession(reader);
            } else {
                System.out.println("Unknown role: " + user.getRole());
            }

        } catch (com.romanimazione.exception.UserNotFoundException | com.romanimazione.exception.DAOException e) {
             System.out.println("Login Failed: " + e.getMessage());
        } catch (Exception e) {
             System.out.println("Unexpected Error: " + e.getMessage());
        }
    }

    private void runAnimatorSession(BufferedReader reader) throws java.io.IOException {
        while (true) {
            System.out.println("\n--- ANIMATOR DASHBOARD ---");
            System.out.println("1. Manage Availability");
            System.out.println("2. Logout");
            System.out.print("Choice: ");
            String choice = reader.readLine();

            if ("1".equals(choice)) {
                handleAvailability(reader);
            } else if ("2".equals(choice)) {
                com.romanimazione.bean.SessionBean.getInstance().setCurrentUser(null);
                System.out.println("Logged out.");
                break;
            } else {
                System.out.println("Invalid choice.");
            }
        }
    }

    private void runAdminSession(BufferedReader reader) throws java.io.IOException {
        while (true) {
            System.out.println("\n--- ADMIN DASHBOARD ---");
            System.out.println("1. (Coming Soon)");
            System.out.println("2. Logout");
            System.out.print("Choice: ");
            String choice = reader.readLine();

            if ("2".equals(choice)) {
                com.romanimazione.bean.SessionBean.getInstance().setCurrentUser(null);
                System.out.println("Logged out.");
                break;
            } else {
                System.out.println("Invalid choice.");
            }
        }
    }

    private void handleAvailability(BufferedReader reader) throws java.io.IOException {
         UserBean currentUser = com.romanimazione.bean.SessionBean.getInstance().getCurrentUser();
         
         while(true) {
             System.out.println("\n--- MANAGE AVAILABILITY ---");
             System.out.println("1. List My Availabilities");
             System.out.println("2. Add Availability");
             System.out.println("3. Update Availability");
             System.out.println("4. Delete Availability");
             System.out.println("5. Back");
             System.out.print("Choice: ");
             String choice = reader.readLine();
             
             com.romanimazione.controller.application.AvailabilityController controller = new com.romanimazione.controller.application.AvailabilityController();
             
             if ("1".equals(choice)) {
                 try {
                     var list = controller.getAvailabilities(currentUser.getUsername());
                     if (list.isEmpty()) System.out.println("No availabilities found.");
                     System.out.printf("%-5s | %-12s | %-20s%n", "ID", "Date", "Time");
                     System.out.println("------------------------------------------");
                     for (var a : list) {
                         System.out.printf("%-5d | %s | %s%n", a.getId(), a.getDate(), (a.isFullDay() ? "Full Day" : a.getStartTime() + " - " + a.getEndTime()));
                     }
                 } catch (Exception e) {
                     System.out.println("Error: " + e.getMessage());
                 }
             } else if ("2".equals(choice)) {
                 try {
                     com.romanimazione.bean.AvailabilityBean bean = new com.romanimazione.bean.AvailabilityBean();
                     bean.setUsername(currentUser.getUsername());
                     System.out.print("Date (YYYY-MM-DD): ");
                     bean.setDate(java.time.LocalDate.parse(reader.readLine()));
                     System.out.print("Full Day? (y/n): ");
                     boolean full = "y".equalsIgnoreCase(reader.readLine());
                     bean.setFullDay(full);
                     if (!full) {
                         System.out.print("Start Time (HH:mm): ");
                         bean.setStartTime(java.time.LocalTime.parse(reader.readLine()));
                         System.out.print("End Time (HH:mm): ");
                         bean.setEndTime(java.time.LocalTime.parse(reader.readLine()));
                     }
                     controller.addAvailability(bean);
                     System.out.println("Added successfully!");
                 } catch (Exception e) {
                     System.out.println("Error: " + e.getMessage());
                 }
             } else if ("3".equals(choice)) {
                 System.out.println("--- Update Availability ---");
                 System.out.print("Enter ID to update: ");
                 try {
                     int id = Integer.parseInt(reader.readLine());
                     com.romanimazione.bean.AvailabilityBean bean = new com.romanimazione.bean.AvailabilityBean();
                     bean.setId(id);
                     bean.setUsername(currentUser.getUsername());
                     
                     System.out.print("New Date (YYYY-MM-DD): ");
                     bean.setDate(java.time.LocalDate.parse(reader.readLine()));
                     System.out.print("Full Day? (y/n): ");
                     boolean full = "y".equalsIgnoreCase(reader.readLine());
                     bean.setFullDay(full);
                     if (!full) {
                         System.out.print("Start Time (HH:mm): ");
                         bean.setStartTime(java.time.LocalTime.parse(reader.readLine()));
                         System.out.print("End Time (HH:mm): ");
                         bean.setEndTime(java.time.LocalTime.parse(reader.readLine()));
                     }
                     controller.updateAvailability(bean);
                     System.out.println("Updated successfully!");
                 } catch (Exception e) {
                     System.out.println("Error: " + e.getMessage());
                 }
             } else if ("4".equals(choice)) {
                 System.out.print("Enter ID to delete: ");
                 try {
                     int id = Integer.parseInt(reader.readLine());
                     com.romanimazione.bean.AvailabilityBean bean = new com.romanimazione.bean.AvailabilityBean();
                     bean.setId(id);
                     bean.setUsername(currentUser.getUsername());
                     controller.deleteAvailability(bean);
                     System.out.println("Deleted successfully!");
                 } catch (Exception e) {
                     System.out.println("Error: " + e.getMessage());
                 }
             } else if ("5".equals(choice)) {
                 break;
             }
         }
    }

    private void handleRegister(BufferedReader reader) throws java.io.IOException {
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
        } catch (com.romanimazione.exception.DuplicateUserException | IllegalArgumentException | com.romanimazione.exception.DAOException e) {
             System.out.println("Registration Failed: " + e.getMessage());
        } catch (Exception e) {
             System.out.println("Unexpected Error: " + e.getMessage());
        }
    }

    @Override
    public void update(String message) {
        System.out.println("[NOTIFICATION]: " + message);
    }
}
