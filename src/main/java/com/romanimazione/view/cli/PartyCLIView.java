package com.romanimazione.view.cli;

import com.romanimazione.bean.PartyBean;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class PartyCLIView {

    private final BufferedReader reader;

    public PartyCLIView() {
        this.reader = new BufferedReader(new InputStreamReader(System.in));
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
        LocalDate date = LocalDate.parse(reader.readLine());

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
        
        return bean;
    }

    public void showPartyList(List<PartyBean> list) {
        System.out.println("\n--- UPCOMING PARTIES ---");
        if (list.isEmpty()) {
            System.out.println("No parties found.");
        } else {
            System.out.printf("%-5s | %-20s | %-15s | %-12s%n", "ID", "Name", "Type", "Date");
            System.out.println("------------------------------------------------------------");
            for (PartyBean p : list) {
                System.out.printf("%-5d | %-20s | %-15s | %s%n", p.getId(), p.getName(), p.getType(), p.getDate());
            }
        }
    }
}
