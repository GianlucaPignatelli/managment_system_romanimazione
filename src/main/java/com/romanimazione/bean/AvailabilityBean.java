package com.romanimazione.bean;


import java.time.LocalDate;
import java.time.LocalTime;

public class AvailabilityBean {
    private int id;
    private String username;
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
    private boolean isFullDay;

    public AvailabilityBean() {}

    public AvailabilityBean(String username, LocalDate date, LocalTime startTime, LocalTime endTime, boolean isFullDay) {
        this.username = username;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.isFullDay = isFullDay;
    }
    
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public LocalTime getStartTime() { return startTime; }
    public void setStartTime(LocalTime startTime) { this.startTime = startTime; }

    public LocalTime getEndTime() { return endTime; }
    public void setEndTime(LocalTime endTime) { this.endTime = endTime; }

    public boolean isFullDay() { return isFullDay; }
    public void setFullDay(boolean fullDay) { isFullDay = fullDay; }
}
