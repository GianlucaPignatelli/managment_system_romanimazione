package com.romanimazione.bean;

import com.romanimazione.entity.Availability;
import java.time.LocalDate;
import java.time.LocalTime;

public class AvailabilityBean extends Availability {

    public AvailabilityBean() {
        super();
    }

    public AvailabilityBean(String username, LocalDate date, LocalTime startTime, LocalTime endTime, boolean isFullDay) {
        super(username, date, startTime, endTime, isFullDay);
    }

    // --- MAPPING METHODS ---
    public static AvailabilityBean fromEntity(Availability entity) {
        if (entity == null) return null;
        AvailabilityBean bean = new AvailabilityBean(
            entity.getUsername(),
            entity.getDate(),
            entity.getStartTime(),
            entity.getEndTime(),
            entity.isFullDay()
        );
        bean.setId(entity.getId());
        return bean;
    }

    public Availability toEntity() {
        Availability entity = new Availability();
        entity.setId(this.getId());
        entity.setUsername(this.getUsername());
        entity.setDate(this.getDate());
        entity.setStartTime(this.getStartTime());
        entity.setEndTime(this.getEndTime());
        entity.setFullDay(this.isFullDay());
        return entity;
    }
}
