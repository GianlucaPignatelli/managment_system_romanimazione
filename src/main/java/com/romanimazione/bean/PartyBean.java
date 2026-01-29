package com.romanimazione.bean;

import com.romanimazione.entity.Party;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 * PartyBean extends Party to avoid code duplication.
 */
public class PartyBean extends Party {

    public PartyBean() {
        super();
    }

    public PartyBean(String name, String type, String address, LocalDate date, String clientName, String clientPhone, LocalTime startTime, LocalTime endTime, Integer childrenCount, int animatorsRequired, String description, double cost) {
        super(name, type, address, date, clientName, clientPhone, startTime, endTime, childrenCount, animatorsRequired, description, cost);
    }

    // Mapping method from Entity to Bean
    public static PartyBean fromEntity(Party entity) {
        if (entity == null) return null;
        PartyBean bean = new PartyBean(
            entity.getName(),
            entity.getType(),
            entity.getAddress(),
            entity.getDate(),
            entity.getClientName(),
            entity.getClientPhone(),
            entity.getStartTime(),
            entity.getEndTime(),
            entity.getChildrenCount(),
            entity.getAnimatorsRequired(),
            entity.getDescription(),
            entity.getCost()
        );
        bean.setId(entity.getId());
        return bean;
    }

    // Mapping method from Bean to Entity
    public Party toEntity() {
        Party entity = new Party();
        entity.setId(this.getId());
        entity.setName(this.getName());
        entity.setType(this.getType());
        entity.setAddress(this.getAddress());
        entity.setDate(this.getDate());
        
        entity.setClientName(this.getClientName());
        entity.setClientPhone(this.getClientPhone());
        entity.setStartTime(this.getStartTime());
        entity.setEndTime(this.getEndTime());
        entity.setChildrenCount(this.getChildrenCount());
        entity.setAnimatorsRequired(this.getAnimatorsRequired());
        entity.setDescription(this.getDescription());
        entity.setCost(this.getCost());
        
        return entity;
    }
}
