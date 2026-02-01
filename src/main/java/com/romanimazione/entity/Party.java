package com.romanimazione.entity;

import java.time.LocalDate;
import java.time.LocalTime;

public class Party {
    private int id;
    private String name;
    private String type;
    private String address;
    private LocalDate date;
    
    // New Fields
    private String clientName;
    private String clientPhone;
    private LocalTime startTime;
    private LocalTime endTime;
    private Integer childrenCount; // Optional
    private int animatorsRequired;
    private String description;
    private double cost;

    public Party() {
        // Default constructor required for framework serialization/deserialization
    }

    // Large constructor removed to avoid Code Smell (Too many parameters). Use Setters.

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public String getClientName() { return clientName; }
    public void setClientName(String clientName) { this.clientName = clientName; }

    public String getClientPhone() { return clientPhone; }
    public void setClientPhone(String clientPhone) { this.clientPhone = clientPhone; }

    public LocalTime getStartTime() { return startTime; }
    public void setStartTime(LocalTime startTime) { this.startTime = startTime; }

    public LocalTime getEndTime() { return endTime; }
    public void setEndTime(LocalTime endTime) { this.endTime = endTime; }

    public Integer getChildrenCount() { return childrenCount; }
    public void setChildrenCount(Integer childrenCount) { this.childrenCount = childrenCount; }

    public int getAnimatorsRequired() { return animatorsRequired; }
    public void setAnimatorsRequired(int animatorsRequired) { this.animatorsRequired = animatorsRequired; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    private java.util.List<String> assignedAnimators = new java.util.ArrayList<>();

    public double getCost() { return cost; }
    public void setCost(double cost) { this.cost = cost; }
    
    public java.util.List<String> getAssignedAnimators() { return assignedAnimators; }
    public void setAssignedAnimators(java.util.List<String> assignedAnimators) { this.assignedAnimators = assignedAnimators; }
    
    private PartyStatus status = PartyStatus.SCHEDULED;
    public PartyStatus getStatus() { return status; }
    public void setStatus(PartyStatus status) { this.status = status; }
}
