package com.romanimazione.controller.graphic;

import com.romanimazione.bean.AvailabilityBean;
import com.romanimazione.bean.CredentialsBean;
import com.romanimazione.bean.SessionBean;
import com.romanimazione.bean.UserBean;
import com.romanimazione.controller.application.AvailabilityController;
import com.romanimazione.controller.application.LoginController;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class CLIController {

    private static final String P_CHOICE = "Choice: ";
    private static final String MSG_INVALID = "Invalid choice.";
    private static final String MSG_ERROR = "Error: ";
    
    private BufferedReader reader;
    private LoginController loginController;
    private AvailabilityController availabilityController;

    public CLIController() {
        this.reader = new BufferedReader(new InputStreamReader(System.in));
        this.loginController = new LoginController();
        this.availabilityController = new AvailabilityController();
    }

    public void start() {
        System.out.println("Welcome to Romanimazione CLI");
        boolean running = true;
        try {
            while (running) {
                System.out.println("\n--- MAIN MENU ---");
                System.out.println("1. Login");
                System.out.println("2. Exit");
                System.out.print(P_CHOICE);
                String input = reader.readLine();

                if ("1".equals(input)) {
                    handleLogin();
                } else if ("2".equals(input)) {
                    running = false;
                    System.out.println("Goodbye!");
                } else {
                    System.out.println(MSG_INVALID);
                }
            }
        } catch (Exception e) {
            System.out.println("System Error: " + e.getMessage());
        }
    }

    private void handleLogin() throws java.io.IOException {
        System.out.print("Username: ");
        String username = reader.readLine();
        System.out.print("Password: ");
        String password = reader.readLine();

        try {
            CredentialsBean creds = new CredentialsBean(username, password);
            UserBean user = loginController.login(creds);
            SessionBean.getInstance().setCurrentUser(user);
            
            System.out.println("Login successful. Role: " + user.getRole());
            
            if ("ANIMATORE".equalsIgnoreCase(user.getRole())) {
                animatorLoop();
            } else if ("AMMINISTRATORE".equalsIgnoreCase(user.getRole())) {
                 adminLoop();
            } else {
                 System.out.println("Unknown role menu.");
            }

        } catch (Exception e) {
            System.out.println(MSG_ERROR + e.getMessage());
        }
    }
    
    private void animatorLoop() throws java.io.IOException {
        boolean loggedIn = true;
        while (loggedIn) {
            System.out.println("\n[ANIMATORE DASHBOARD]");
            System.out.println("1. Manage Availability");
            System.out.println("2. Logout");
            System.out.print(P_CHOICE);
            String subInput = reader.readLine();
            
            if ("1".equals(subInput)) {
                manageAvailability();
            } else if ("2".equals(subInput)) {
                loggedIn = false;
                SessionBean.getInstance().setCurrentUser(null);
                System.out.println("Logged out.");
            } else {
                System.out.println(MSG_INVALID);
            }
        }
    }

    private void adminLoop() throws java.io.IOException {
         System.out.println("\n[ADMIN DASHBOARD]");
         System.out.println("Admin features not implemented in CLI yet.");
         System.out.println("Press Enter to logout...");
         reader.readLine();
         SessionBean.getInstance().setCurrentUser(null);
    }

    private void manageAvailability() throws java.io.IOException {
        boolean back = false;
        while (!back) {
            System.out.println("\n[MANAGE AVAILABILITY]");
            System.out.println("1. Add Availability");
            System.out.println("2. List My Availabilities");
            System.out.println("3. Update Availability");
            System.out.println("4. Delete Availability");
            System.out.println("5. Back");
            System.out.print(P_CHOICE);
            String input = reader.readLine();

            try {
                switch (input) {
                    case "1": addAvailabilityCLI(); break;
                    case "2": listAvailabilityCLI(); break;
                    case "3": updateAvailabilityCLI(); break;
                    case "4": deleteAvailabilityCLI(); break;
                    case "5": back = true; break;
                    default: System.out.println(MSG_INVALID);
                }
            } catch (Exception e) {
                System.out.println(MSG_ERROR + e.getMessage());
            }
        }
    }

    private void addAvailabilityCLI() throws java.io.IOException, com.romanimazione.exception.DAOException, com.romanimazione.exception.InvalidAvailabilityException {
        AvailabilityBean bean = promptForAvailabilityDetails();
        bean.setUsername(SessionBean.getInstance().getCurrentUser().getUsername());
        availabilityController.addAvailability(bean);
        System.out.println("Availability added successfully.");
    }
    
    private void updateAvailabilityCLI() throws java.io.IOException, com.romanimazione.exception.DAOException, com.romanimazione.exception.InvalidAvailabilityException {
        System.out.print("Enter ID to update: ");
        int id = Integer.parseInt(reader.readLine());
        
        AvailabilityBean bean = promptForAvailabilityDetails();
        bean.setId(id);
        bean.setUsername(SessionBean.getInstance().getCurrentUser().getUsername());
        
        availabilityController.updateAvailability(bean);
        System.out.println("Availability updated successfully.");
    }
    
    private void deleteAvailabilityCLI() throws java.io.IOException, com.romanimazione.exception.DAOException {
         System.out.print("Enter ID to delete: ");
         int id = Integer.parseInt(reader.readLine());
         AvailabilityBean bean = new AvailabilityBean();
         bean.setId(id);
         bean.setUsername(SessionBean.getInstance().getCurrentUser().getUsername());
         
         availabilityController.deleteAvailability(bean);
         System.out.println("Availability deleted successfully.");
    }

    private void listAvailabilityCLI() throws com.romanimazione.exception.DAOException {
        String user = SessionBean.getInstance().getCurrentUser().getUsername();
        List<AvailabilityBean> list = availabilityController.getAvailabilities(user);
        if (list.isEmpty()) {
            System.out.println("No availabilities found.");
        } else {
            System.out.printf("%-5s | %-12s | %-20s%n", "ID", "Date", "Time");
            System.out.println("------------------------------------------");
            for (AvailabilityBean b : list) {
                String timeInfo = b.isFullDay() ? "Full Day" : b.getStartTime() + " - " + b.getEndTime();
                System.out.printf("%-5d | %s | %s%n", b.getId(), b.getDate(), timeInfo);
            }
        }
    }

    private AvailabilityBean promptForAvailabilityDetails() throws java.io.IOException {
        System.out.print("Date (YYYY-MM-DD): ");
        LocalDate date = LocalDate.parse(reader.readLine());
        
        System.out.print("Full Day? (y/n): ");
        boolean isFull = "y".equalsIgnoreCase(reader.readLine());
        
        AvailabilityBean bean = new AvailabilityBean();
        bean.setDate(date);
        bean.setFullDay(isFull);
        
        if (!isFull) {
             System.out.print("Start Time (HH:mm): ");
             bean.setStartTime(LocalTime.parse(reader.readLine()));
             System.out.print("End Time (HH:mm): ");
             bean.setEndTime(LocalTime.parse(reader.readLine()));
        }
        return bean;
    }
}
