package com.romanimazione.view.cli;

import com.romanimazione.bean.AvailabilityBean;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.util.List;

public class AvailabilityCLIView {

    private final BufferedReader reader;

    public AvailabilityCLIView() {
        this.reader = new BufferedReader(new InputStreamReader(System.in));
    }

    public String showAvailabilityMenuAndGetChoice() throws IOException {
        System.out.println("\n[MANAGE AVAILABILITY]");
        System.out.println("1. Add Availability");
        System.out.println("2. List My Availabilities");
        System.out.println("3. Update Availability");
        System.out.println("4. Delete Availability");
        System.out.println("5. Back");
        System.out.print("Choice: ");
        return reader.readLine();
    }

    public int getIdInput(String action) throws IOException {
         System.out.print("Enter ID to " + action + ": ");
         return Integer.parseInt(reader.readLine());
    }

    public AvailabilityBean getAvailabilityDetails() throws IOException {
        System.out.print("Date (YYYY-MM-DD): ");
        LocalDate date = LocalDate.parse(reader.readLine());
        
        System.out.print("Full Day? (y/n): ");
        boolean isFull = "y".equalsIgnoreCase(reader.readLine());
        
        AvailabilityBean bean = new AvailabilityBean();
        bean.setDate(date.toString());
        bean.setIsFullDay(String.valueOf(isFull));
        
        if (!isFull) {
             System.out.print("Start Time (HH:mm): ");
             bean.setStartTime(reader.readLine());
             System.out.print("End Time (HH:mm): ");
             bean.setEndTime(reader.readLine());
        }
        return bean;
    }
    
    public void showAvailabilityList(List<AvailabilityBean> list) {
        if (list.isEmpty()) {
            System.out.println("No availabilities found.");
        } else {
            System.out.printf("%-5s | %-12s | %-20s%n", "ID", "Date", "Time");
            System.out.println("------------------------------------------");
            for (AvailabilityBean b : list) {
                String timeInfo = Boolean.parseBoolean(b.getIsFullDay()) ? "Full Day" : b.getStartTime() + " - " + b.getEndTime();
                System.out.printf("%-5s | %s | %s%n", b.getId(), b.getDate(), timeInfo);
            }
        }
    }
}
