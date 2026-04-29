package com.romanimazione.bean;

import com.romanimazione.exception.InvalidPartyException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class PartyBean {

    private String id;
    private String name;
    private String type;
    private String address;
    private String date;
    private String clientName;
    private String clientPhone;
    private String startTime;
    private String endTime;
    private String childrenCount;
    private String animatorsRequired;
    private String description;
    private String cost;
    private String status;
    private Map<String, String> assignmentStatuses;
    private Map<String, String> assignmentTimestamps;

    public PartyBean() {
        this.assignmentStatuses = new HashMap<>();
        this.assignmentTimestamps = new HashMap<>();
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public String getClientName() { return clientName; }
    public void setClientName(String clientName) { this.clientName = clientName; }

    public String getClientPhone() { return clientPhone; }
    public void setClientPhone(String clientPhone) { this.clientPhone = clientPhone; }

    public String getStartTime() { return startTime; }
    public void setStartTime(String startTime) { this.startTime = startTime; }

    public String getEndTime() { return endTime; }
    public void setEndTime(String endTime) { this.endTime = endTime; }

    public String getChildrenCount() { return childrenCount; }
    public void setChildrenCount(String childrenCount) { this.childrenCount = childrenCount; }

    public String getAnimatorsRequired() { return animatorsRequired; }
    public void setAnimatorsRequired(String animatorsRequired) { this.animatorsRequired = animatorsRequired; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getCost() { return cost; }
    public void setCost(String cost) { this.cost = cost; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Map<String, String> getAssignmentStatuses() { return assignmentStatuses; }
    public void setAssignmentStatuses(Map<String, String> assignmentStatuses) { this.assignmentStatuses = assignmentStatuses; }

    public Map<String, String> getAssignmentTimestamps() { return assignmentTimestamps; }
    public void setAssignmentTimestamps(Map<String, String> assignmentTimestamps) { this.assignmentTimestamps = assignmentTimestamps; }


    private static final List<String> ALLOWED_TYPES = Arrays.asList(
        "Full Party", "Smart Party", "Consegna", "Ritiro in sede", "Servizio carretti", "Evento in piazza"
    );

    public void validateSyntax() throws InvalidPartyException {
        // Mandatory Strings
        if (this.name == null || this.name.trim().isEmpty()) throw new InvalidPartyException("Party name is required.");
        if (this.address == null || this.address.trim().isEmpty()) throw new InvalidPartyException("Address is required.");
        if (this.clientName == null || this.clientName.trim().isEmpty()) throw new InvalidPartyException("Client Name is required.");
        
        // Regex format
        if (this.clientPhone == null || !this.clientPhone.matches("^\\d{10}$")) {
            throw new InvalidPartyException("Client Phone must be exactly 10 digits.");
        }

        // Parse and validate logic boundaries
        try {
            int animators = Integer.parseInt(this.animatorsRequired);
            if (animators < 1) throw new InvalidPartyException("At least 1 animator is required.");
        } catch (NumberFormatException e) {
            throw new InvalidPartyException("Animators required must be a number.");
        }

        if (this.childrenCount != null && !this.childrenCount.trim().isEmpty()) {
            try {
                Integer.parseInt(this.childrenCount);
            } catch (NumberFormatException e) {
                throw new InvalidPartyException("Children count must be a number.");
            }
        }

        try {
            double c = Double.parseDouble(this.cost);
            if (c < 0) throw new InvalidPartyException("Cost cannot be negative.");
        } catch (NumberFormatException | NullPointerException e) {
            throw new InvalidPartyException("Cost must be a valid number.");
        }

        if (this.type == null || !ALLOWED_TYPES.contains(this.type)) {
            throw new InvalidPartyException("Invalid Party Type. Allowed: " + ALLOWED_TYPES);
        }

        LocalDate parsedDate = null;
        try {
            parsedDate = LocalDate.parse(this.date);
            if (parsedDate.isBefore(LocalDate.now())) {
                throw new InvalidPartyException("Date must be today or in the future.");
            }
        } catch (DateTimeParseException | NullPointerException e) {
            throw new InvalidPartyException("Date is missing or invalid.");
        }

        LocalTime startT = null;
        LocalTime endT = null;
        try {
            startT = LocalTime.parse(this.startTime);
            endT = LocalTime.parse(this.endTime);
        } catch (DateTimeParseException | NullPointerException e) {
            throw new InvalidPartyException("Start/End Time missing or invalid format (HH:mm).");
        }

        if (!endT.isAfter(startT)) {
            throw new InvalidPartyException("End Time must be after Start Time.");
        }
        
        if (parsedDate.isEqual(LocalDate.now()) && startT.isBefore(LocalTime.now())) {
            throw new InvalidPartyException("Cannot schedule a party in the past on the current day.");
        }
    }
}
