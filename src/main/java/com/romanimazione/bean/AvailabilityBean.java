package com.romanimazione.bean;

import com.romanimazione.exception.InvalidAvailabilityException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;

public class AvailabilityBean {

    private String id;
    private String username;
    private String date;
    private String startTime;
    private String endTime;
    private String isFullDay;

    public AvailabilityBean() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public String getStartTime() { return startTime; }
    public void setStartTime(String startTime) { this.startTime = startTime; }

    public String getEndTime() { return endTime; }
    public void setEndTime(String endTime) { this.endTime = endTime; }

    public String getIsFullDay() { return isFullDay; }
    public void setIsFullDay(String isFullDay) { this.isFullDay = isFullDay; }

    public void validateSyntax() throws InvalidAvailabilityException {
        if (this.username == null || this.username.trim().isEmpty()) {
            throw new InvalidAvailabilityException("Username is required.");
        }
        if (this.date == null || this.date.trim().isEmpty()) {
            throw new InvalidAvailabilityException("Date is required.");
        }
        
        try {
            LocalDate.parse(this.date);
        } catch (DateTimeParseException | NullPointerException e) {
            throw new InvalidAvailabilityException("Date is missing or invalid.");
        }

        boolean fullDay = Boolean.parseBoolean(this.isFullDay);
        if (!fullDay) {
            if (this.startTime == null || this.startTime.trim().isEmpty() ||
                this.endTime == null || this.endTime.trim().isEmpty()) {
                throw new InvalidAvailabilityException("Start Time and End Time are required for partial day availability.");
            }
            try {
                LocalTime start = LocalTime.parse(this.startTime);
                LocalTime end = LocalTime.parse(this.endTime);
                if (!end.isAfter(start)) {
                    throw new InvalidAvailabilityException("End Time must be after Start Time.");
                }
            } catch (DateTimeParseException | NullPointerException e) {
                throw new InvalidAvailabilityException("Time format is invalid. Use HH:mm.");
            }
        }
    }
}
