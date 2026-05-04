package com.romanimazione.entity;

import java.time.LocalDate;
import java.time.LocalTime;

public class Party {
    private int id;
    private String name;
    private String type;
    private String address;
    private LocalDate date;
    private String clientName;
    private String clientPhone;
    private LocalTime startTime;
    private LocalTime endTime;
    private Integer childrenCount; // Optional
    private int animatorsRequired;
    private String description;
    private double cost;
    private String equipmentCategory;

    public Party() {
        // Default constructor required for framework serialization/deserialization
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return this.name; }
    public void setName(String valName) { this.name = valName; }

    public String getType() { return this.type; }
    public void setType(String valType) { this.type = valType; }

    public String getAddress() { return this.address; }
    public void setAddress(String valAddress) { this.address = valAddress; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public String getClientName() { return this.clientName; }
    public void setClientName(String valClientName) { this.clientName = valClientName; }

    public String getClientPhone() { return this.clientPhone; }
    public void setClientPhone(String valClientPhone) { this.clientPhone = valClientPhone; }

    public LocalTime getStartTime() { return startTime; }
    public void setStartTime(LocalTime startTime) { this.startTime = startTime; }

    public LocalTime getEndTime() { return endTime; }
    public void setEndTime(LocalTime endTime) { this.endTime = endTime; }

    public Integer getChildrenCount() { return childrenCount; }
    public void setChildrenCount(Integer childrenCount) { this.childrenCount = childrenCount; }

    public int getAnimatorsRequired() { return animatorsRequired; }
    public void setAnimatorsRequired(int animatorsRequired) { this.animatorsRequired = animatorsRequired; }

    public String getDescription() { return this.description; }
    public void setDescription(String valDesc) { this.description = valDesc; }

    private java.util.Map<String, AssignmentStatus> assignmentStatuses = new java.util.HashMap<>();
    private java.util.Map<String, java.time.LocalDateTime> assignmentTimestamps = new java.util.HashMap<>();

    public double getCost() { return cost; }
    public void setCost(double cost) { this.cost = cost; }

    public String getEquipmentCategory() { return equipmentCategory; }
    public void setEquipmentCategory(String equipmentCategory) { this.equipmentCategory = equipmentCategory; }
    
    // Backward compatibility: Returns all animators involved (Pending/Accepted/Rejected)
    @com.fasterxml.jackson.annotation.JsonIgnore
    public java.util.List<String> getAssignedAnimators() { 
        return new java.util.ArrayList<>(assignmentStatuses.keySet()); 
    }
    
    // For JSON serialization/deserialization if needed, but better to expose the map directly
    public java.util.Map<String, AssignmentStatus> getAssignmentStatuses() { return assignmentStatuses; }
    public void setAssignmentStatuses(java.util.Map<String, AssignmentStatus> assignmentStatuses) { this.assignmentStatuses = assignmentStatuses; }
    
    public java.util.Map<String, java.time.LocalDateTime> getAssignmentTimestamps() { return assignmentTimestamps; }
    public void setAssignmentTimestamps(java.util.Map<String, java.time.LocalDateTime> assignmentTimestamps) { this.assignmentTimestamps = assignmentTimestamps; }
    
    private PartyStatus status = PartyStatus.SCHEDULED;
    public PartyStatus getStatus() { return status; }
    public void setStatus(PartyStatus status) { this.status = status; }
}
