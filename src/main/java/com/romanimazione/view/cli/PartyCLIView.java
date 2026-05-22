package com.romanimazione.view.cli;

import com.romanimazione.bean.PartyBean;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.util.List;

public class PartyCLIView {

    private final BufferedReader reader;

    public PartyCLIView() {
        this.reader = new BufferedReader(new InputStreamReader(System.in));
    }
    
    public LocalDate promptForDate(String label) throws IOException {
        System.out.print(label + " (YYYY-MM-DD or leave empty): ");
        String input = reader.readLine();
        if (input == null || input.trim().isEmpty()) {
            return null;
        }
        try {
            return LocalDate.parse(input.trim());
        } catch (Exception e) {
            System.out.println("Invalid date format. Using None.");
            return null;
        }
    }

    public PartyBean getPartyDetails(List<String> allowedTypes) throws IOException {
        System.out.println("\n--- CREATE NEW PARTY ---");
        
        System.out.print("Event Name: ");
        String name = reader.readLine();

        System.out.println("Allowed Types: " + allowedTypes);
        System.out.print("Type: ");
        String type = reader.readLine();

        System.out.print("Address: ");
        String address = reader.readLine();

        System.out.print("Date (YYYY-MM-DD): ");
        String date = reader.readLine();

        System.out.print("Client Name: ");
        String clientName = reader.readLine();

        System.out.print("Client Phone: ");
        String clientPhone = reader.readLine();

        System.out.print("Start Time (HH:mm): ");
        String startTime = reader.readLine();

        System.out.print("End Time (HH:mm): ");
        String endTime = reader.readLine();

        System.out.print("Children Count (Enter for none): ");
        String childrenInput = reader.readLine();
        String children = (childrenInput == null || childrenInput.trim().isEmpty()) ? null : childrenInput.trim();

        System.out.print("Animators Required: ");
        String animators = reader.readLine();

        System.out.print("Description: ");
        String description = reader.readLine();

        System.out.print("Total Cost: ");
        String cost = reader.readLine();

        System.out.println("Equipment Category Req:");
        System.out.println("1. borsone giochi");
        System.out.println("2. carretto");
        System.out.println("3. borsa magia");
        System.out.println("4. cassa audio");
        System.out.println("5. gonfiabile");
        System.out.print("Select (1-5 or press Enter to skip): ");
        String catChoice = reader.readLine();
        String eqCat = "";
        if (catChoice != null) {
            switch (catChoice.trim()) {
                case "1": eqCat = "borsone giochi"; break;
                case "2": eqCat = "carretto"; break;
                case "3": eqCat = "borsa magia"; break;
                case "4": eqCat = "cassa audio"; break;
                case "5": eqCat = "gonfiabile"; break;
                default: break;
            }
        }

        PartyBean bean = new PartyBean();
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
        bean.setEquipmentCategory(eqCat);
        
        return bean;
    }

    public void showPartyList(List<PartyBean> list) {
        System.out.println("\n--- UPCOMING PARTIES ---");
        if (list.isEmpty()) {
            System.out.println("No parties found.");
        } else {
            System.out.printf("%-5s | %-20s | %-12s | %-12s | %-15s | %s%n", "ID", "Name", "Date", "Status", "Staff (Req)", "Assigned");
            System.out.println("-----------------------------------------------------------------------------------------------");
            for (PartyBean p : list) {
                // Build text description of assignments with status
                StringBuilder sb = new StringBuilder();
                if (p.getAssignmentStatuses().isEmpty()) {
                    sb.append("None");
                } else {
                    p.getAssignmentStatuses().forEach((user, status) -> {
                        sb.append(user).append("(").append(status).append(") ");
                    });
                }
                String assignedInfo = sb.toString();
                String staffInfo = p.getAnimatorsRequired() + " req";
                
                System.out.printf("%-5s | %-20s | %-12s | %-12s | %-15s | %s%n", 
                    p.getId(), p.getName(), p.getDate(), p.getStatus(), staffInfo, assignedInfo);
            }
        }
    }

    public int askSelectPartyId() throws IOException {
        System.out.print("Enter Party ID to view details (or 0 to skip/back): ");
        try {
            return Integer.parseInt(reader.readLine());
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    public void showEligibleAnimators(List<com.romanimazione.bean.UserBean> animators) {
        System.out.println("\n--- ELIGIBLE ANIMATORS ---");
        if (animators.isEmpty()) {
            System.out.println("No eligible animators found.");
            return;
        }
        for (int i = 0; i < animators.size(); i++) {
             com.romanimazione.bean.UserBean u = animators.get(i);
             String note = Boolean.parseBoolean(u.getIsTimeCompatible()) ? "[MATCH]" : "* (TIME MISMATCH)";
             
             String colorReset = "\u001B[0m";
             String colorRed = "\u001B[31m";
             String colorGreen = "\u001B[32m";
             
             // Simple color coding if terminal supports it (usually works in VSCode/IntelliJ)
             if (!Boolean.parseBoolean(u.getIsTimeCompatible())) {
                 System.out.printf("%d. %s (%s %s) %s%s%s%n", i+1, u.getUsername(), u.getNome(), u.getCognome(), colorRed, note, colorReset);
             } else {
                 System.out.printf("%d. %s (%s %s) %s%s%s%n", i+1, u.getUsername(), u.getNome(), u.getCognome(), colorGreen, note, colorReset);
             }
        }
    }

    public int askAnimatorSelection(int max) throws IOException {
        System.out.print("Select User # to assign (0 to cancel): ");
        try {
            int choice = Integer.parseInt(reader.readLine());
            if (choice < 0 || choice > max) return 0;
            return choice;
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    public void showJobOffers(List<PartyBean> list, String animatorUsername) {
        System.out.println("\n--- JOB OFFERS (PENDING) ---");
        if (list.isEmpty()) {
            System.out.println("No pending job offers.");
        } else {
            System.out.printf("%-5s | %-12s | %-8s | %-20s | %-15s%n", "ID", "Date", "Time", "Details", "Expires In");
            System.out.println("-------------------------------------------------------------------------");
            for (int i = 0; i < list.size(); i++) {
                PartyBean p = list.get(i);
                String extra = "";
                String timestampStr = p.getAssignmentTimestamps().get(animatorUsername);
                java.time.LocalDateTime assignedAt = timestampStr != null ? java.time.LocalDateTime.parse(timestampStr) : null;
                if (assignedAt != null) {
                     java.time.Duration rem = java.time.Duration.between(java.time.LocalDateTime.now(), assignedAt.plusHours(24));
                     if (!rem.isNegative()) {
                         extra = String.format("%dh %dm", rem.toHours(), rem.toMinutes() % 60);
                     } else {
                         extra = "Expired";
                     }
                }
                System.out.printf("%d. [ID:%s] | %s | %s | %-20s | %s%n", 
                    i+1, p.getId(), p.getDate(), p.getStartTime(), p.getName(), extra);
            }
        }
    }
    
    public int askJobOfferSelection(int max) throws IOException {
        System.out.print("Select Offer # to respond to (0 to back): ");
        try {
            String input = reader.readLine();
            int choice = Integer.parseInt(input);
            if (choice < 0 || choice > max) return 0;
            return choice;
        } catch (NumberFormatException e) {
            return 0;
        }
    }
    
    public String askAcceptOrReject() throws IOException {
        System.out.print("Do you want to (A)ccept or (R)eject? ");
        String input = reader.readLine();
        return (input == null) ? "" : input.trim().toUpperCase();
    }

    public boolean askForceAssignment() throws IOException {
        System.out.println("Nessun animator disponibile. Vuoi forzare l'assegnazione mostrando tutti gli animatori del sistema? (Y/N): ");
        String answer = reader.readLine();
        return "Y".equalsIgnoreCase(answer != null ? answer.trim() : "");
    }

    public void showPartyDetails(PartyBean p) {
        System.out.println("\n=== PARTY DETAILS ===");
        System.out.println("ID: " + p.getId());
        System.out.println("Name: " + p.getName());
        System.out.println("Type: " + p.getType());
        System.out.println("Date: " + p.getDate());
        System.out.println("Time: " + p.getStartTime() + " to " + p.getEndTime());
        System.out.println("Address: " + p.getAddress());
        System.out.println("Client: " + p.getClientName() + " (" + p.getClientPhone() + ")");
        System.out.println("Cost: €" + p.getCost());
        System.out.println("Children Count: " + (p.getChildrenCount() != null ? p.getChildrenCount() : "Not specified"));
        System.out.println("Description: " + (p.getDescription() != null ? p.getDescription() : "None"));
        System.out.println("Status: " + p.getStatus());
        
        System.out.println("\n[STAFF]");
        System.out.println("Animators Required: " + p.getAnimatorsRequired());
        if (p.getAssignmentStatuses().isEmpty()) {
            System.out.println("Assigned: None");
        } else {
            System.out.println("Assigned Animators:");
            p.getAssignmentStatuses().forEach((user, status) -> printAnimatorStatus(user, status, p));
        }
        System.out.println("=====================\n");
    }

    private void printAnimatorStatus(String user, String status, PartyBean p) {
        String extraInfo = "";
        if (com.romanimazione.entity.AssignmentStatus.PENDING.name().equals(status)) {
            String timestampStr = p.getAssignmentTimestamps().get(user);
            java.time.LocalDateTime assignedAt = timestampStr != null ? java.time.LocalDateTime.parse(timestampStr) : null;
            if (assignedAt != null) {
                java.time.Duration rem = java.time.Duration.between(java.time.LocalDateTime.now(), assignedAt.plusHours(24));
                if (!rem.isNegative()) {
                    extraInfo = String.format(" - Scade tra %dh %dm", rem.toHours(), rem.toMinutes() % 60);
                } else {
                    extraInfo = " - Scaduto";
                }
            }
        }
        System.out.println(" - " + user + " (Status: " + status + extraInfo + ")");
    }

    public int askPartyAction() throws IOException {
        System.out.println("Actions for this party:");
        System.out.println("1. Assign Animator");
        System.out.println("2. Cancel Party");
        System.out.println("3. Back/Skip");
        System.out.print("Choose action: ");
        try {
            return Integer.parseInt(reader.readLine());
        } catch (NumberFormatException e) {
            return 3;
        }
    }
}
