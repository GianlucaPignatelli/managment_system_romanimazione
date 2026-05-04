package com.romanimazione.view.cli;

import com.romanimazione.bean.EquipmentBean;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

public class EquipmentCLIView {

    private final BufferedReader reader;

    public EquipmentCLIView() {
        this.reader = new BufferedReader(new InputStreamReader(System.in));
    }

    public String showEquipmentMenuAndGetChoice() throws IOException {
        System.out.println("\n--- EQUIPMENT MANAGEMENT (MAGAZZINO) ---");
        System.out.println("1. View All Equipment");
        System.out.println("2. Add Equipment");
        System.out.println("3. Update Equipment");
        System.out.println("4. Delete Equipment");
        System.out.println("5. Back");
        System.out.print("Choice: ");
        return reader.readLine();
    }

    public void showEquipmentList(List<EquipmentBean> eqList) {
        if (eqList.isEmpty()) {
            System.out.println("No equipment found in warehouse.");
            return;
        }
        System.out.printf("%-5s | %-25s | %-15s | %-10s | %-15s | %-15s%n", 
                "ID", "Name", "Category", "Quantity", "Condition", "Admin");
        System.out.println("--------------------------------------------------------------------------------------------------");
        for (EquipmentBean e : eqList) {
            System.out.printf("%-5s | %-25s | %-15s | %-10s | %-15s | %-15s%n",
                    e.getId(), e.getName(), e.getCategory(), e.getQuantity(), e.getCondition(), e.getAdminUsername());
        }
    }

    public EquipmentBean getEquipmentDetails() throws IOException {
        EquipmentBean bean = new EquipmentBean();
        System.out.print("Name: ");
        bean.setName(reader.readLine());
        
        System.out.println("Category:");
        System.out.println("1. borsone giochi");
        System.out.println("2. carretto");
        System.out.println("3. borsa magia");
        System.out.println("4. cassa audio");
        System.out.println("5. gonfiabile");
        System.out.print("Select (1-5): ");
        String catChoice = reader.readLine();
        if (catChoice == null) catChoice = "";
        switch (catChoice) {
            case "1": bean.setCategory("borsone giochi"); break;
            case "2": bean.setCategory("carretto"); break;
            case "3": bean.setCategory("borsa magia"); break;
            case "4": bean.setCategory("cassa audio"); break;
            case "5": bean.setCategory("gonfiabile"); break;
            default: bean.setCategory(""); // Will fail validation
        }

        System.out.print("Quantity: ");
        bean.setQuantity(reader.readLine());
        
        System.out.println("Condition:");
        System.out.println("1. ottimo");
        System.out.println("2. buono");
        System.out.println("3. discreto");
        System.out.println("4. rotto");
        System.out.print("Select (1-4): ");
        String condChoice = reader.readLine();
        if (condChoice == null) condChoice = "";
        switch (condChoice) {
            case "1": bean.setCondition("ottimo"); break;
            case "2": bean.setCondition("buono"); break;
            case "3": bean.setCondition("discreto"); break;
            case "4": bean.setCondition("rotto"); break;
            default: bean.setCondition(""); // Will fail validation if condition is mandatory
        }
        
        return bean;
    }

    public String getIdInput(String action) throws IOException {
        System.out.print("Enter Equipment ID to " + action + ": ");
        return reader.readLine();
    }
}
