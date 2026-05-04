package com.romanimazione.entity;

public class Equipment {
    private int id;
    private String name;
    private String category;
    private int quantity;
    private String condition;

    private String adminUsername; // Colui che l'ha inserito o ne è responsabile
    
    public Equipment() {}

    public Equipment(int id, String name, String category, int quantity, String condition, String adminUsername) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.quantity = quantity;
        this.condition = condition;
        this.adminUsername = adminUsername;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public String getCondition() { return condition; }
    public void setCondition(String condition) { this.condition = condition; }

    public String getAdminUsername() { return adminUsername; }
    public void setAdminUsername(String adminUsername) { this.adminUsername = adminUsername; }
}
