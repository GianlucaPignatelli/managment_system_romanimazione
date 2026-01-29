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
                System.out.println("2. Register");
                System.out.println("3. Exit");
                System.out.print(P_CHOICE);
                String input = reader.readLine();

                if ("1".equals(input)) {
                    handleLogin();
                } else if ("2".equals(input)) {
                    handleRegistration();
                } else if ("3".equals(input)) {
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
        boolean loggedIn = true;
        com.romanimazione.controller.application.PartyController partyController = new com.romanimazione.controller.application.PartyController();

        while (loggedIn) {
            System.out.println("\n[ADMIN DASHBOARD]");
            System.out.println("1. Create New Party");
            System.out.println("2. List All Parties");
            System.out.println("3. Logout");
            System.out.print(P_CHOICE);
            String input = reader.readLine();

            try {
                if ("1".equals(input)) {
                    createPartyCLI(partyController);
                } else if ("2".equals(input)) {
                    listPartiesCLI(partyController);
                } else if ("3".equals(input)) {
                    loggedIn = false;
                    SessionBean.getInstance().setCurrentUser(null);
                    System.out.println("Logged out.");
                } else {
                    System.out.println(MSG_INVALID);
                }
            } catch (Exception e) {
                System.out.println(MSG_ERROR + e.getMessage());
            }
        }
    }

    private void createPartyCLI(com.romanimazione.controller.application.PartyController controller) throws java.io.IOException, com.romanimazione.exception.InvalidPartyException, com.romanimazione.exception.DAOException {
        System.out.println("\n--- CREATE NEW PARTY ---");
        
        System.out.print("Event Name: ");
        String name = reader.readLine();

        System.out.println("Allowed Types: " + controller.getPartyTypes());
        System.out.print("Type: ");
        String type = reader.readLine();

        System.out.print("Address: ");
        String address = reader.readLine();

        System.out.print("Date (YYYY-MM-DD): ");
        LocalDate date = LocalDate.parse(reader.readLine());

        // New Fields
        System.out.print("Client Name: ");
        String clientName = reader.readLine();

        System.out.print("Client Phone: ");
        String clientPhone = reader.readLine();

        System.out.print("Start Time (HH:mm): ");
        LocalTime startTime = LocalTime.parse(reader.readLine());

        System.out.print("End Time (HH:mm): ");
        LocalTime endTime = LocalTime.parse(reader.readLine());

        System.out.print("Children Count (Enter for none): ");
        String childrenInput = reader.readLine();
        Integer children = (childrenInput.isEmpty()) ? null : Integer.parseInt(childrenInput);

        System.out.print("Animators Required: ");
        int animators = Integer.parseInt(reader.readLine());

        System.out.print("Description: ");
        String description = reader.readLine();

        System.out.print("Total Cost: ");
        double cost = Double.parseDouble(reader.readLine());

        com.romanimazione.bean.PartyBean bean = new com.romanimazione.bean.PartyBean();
        bean.setName(name);
        bean.setType(type);
        bean.setAddress(address);
        bean.setDate(date);
        bean.setClientName(clientName);
        bean.setClientPhone(clientPhone);
        bean.setStartTime(startTime);
        bean.setEndTime(endTime);
        bean.setChildrenCount(children);
        bean.setAnimatorsRequired(animators);
        bean.setDescription(description);
        bean.setCost(cost);

        controller.createParty(bean);
        System.out.println("Party created successfully!");
    }

    private void listPartiesCLI(com.romanimazione.controller.application.PartyController controller) throws com.romanimazione.exception.DAOException {
        System.out.println("\n--- UPCOMING PARTIES ---");
        List<com.romanimazione.bean.PartyBean> list = controller.getAllParties();
        if (list.isEmpty()) {
            System.out.println("No parties found.");
        } else {
            System.out.printf("%-5s | %-20s | %-15s | %-12s%n", "ID", "Name", "Type", "Date");
            System.out.println("------------------------------------------------------------");
            for (com.romanimazione.bean.PartyBean p : list) {
                System.out.printf("%-5d | %-20s | %-15s | %s%n", p.getId(), p.getName(), p.getType(), p.getDate());
            }
        }
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

    private void handleRegistration() throws java.io.IOException {
        System.out.println("\n--- REGISTER ---");
        System.out.print("Username: ");
        String username = reader.readLine();
        System.out.print("Password: ");
        String password = reader.readLine();
        System.out.print("Name: ");
        String name = reader.readLine();
        System.out.print("Surname: ");
        String surname = reader.readLine();
        System.out.print("Email: ");
        String email = reader.readLine();
        
        System.out.println("Role (1. ANIMATORE, 2. ADMIN): ");
        String roleInput = reader.readLine();
        String role = "ANIMATORE"; // Default
        if ("2".equals(roleInput)) role = "AMMINISTRATORE";

        com.romanimazione.bean.UserBean userBean = new com.romanimazione.bean.UserBean();
        userBean.setUsername(username);
        userBean.setPassword(password);
        userBean.setNome(name);
        userBean.setCognome(surname);
        userBean.setEmail(email);
        userBean.setRole(role);

        try {
            com.romanimazione.controller.application.RegisterController regController = new com.romanimazione.controller.application.RegisterController();
            regController.register(userBean);
            System.out.println("Registration successful! You can now login.");
        } catch (Exception e) {
            System.out.println(MSG_ERROR + e.getMessage());
        }
    }
}
