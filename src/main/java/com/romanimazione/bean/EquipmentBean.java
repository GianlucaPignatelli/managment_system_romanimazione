package com.romanimazione.bean;

public class EquipmentBean {
    private String id;
    private String name;
    private String category;
    private String quantity;
    private String condition;
    private String adminUsername;

    public EquipmentBean() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getQuantity() { return quantity; }
    public void setQuantity(String quantity) { this.quantity = quantity; }

    public String getCondition() { return condition; }
    public void setCondition(String condition) { this.condition = condition; }

    public String getAdminUsername() { return adminUsername; }
    public void setAdminUsername(String adminUsername) { this.adminUsername = adminUsername; }

    public void validateSyntax() throws IllegalArgumentException {
        if (this.name == null || this.name.trim().isEmpty()) {
            throw new IllegalArgumentException("Equipment name is required.");
        }
        if (this.category == null || this.category.trim().isEmpty()) {
            throw new IllegalArgumentException("Category is required.");
        }
        if (this.adminUsername == null || this.adminUsername.trim().isEmpty()) {
            throw new IllegalArgumentException("Admin Username is required.");
        }
        if (this.quantity != null && !this.quantity.trim().isEmpty()) {
            try {
                int q = Integer.parseInt(this.quantity);
                if (q < 0) throw new IllegalArgumentException("Quantity cannot be negative.");
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Quantity must be a valid integer.");
            }
        }
    }
}
