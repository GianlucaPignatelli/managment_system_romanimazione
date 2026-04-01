package com.romanimazione.bean;

import com.romanimazione.entity.Party;

/**
 * PartyBean extends Party to avoid code duplication.
 */
public class PartyBean extends Party {

    public PartyBean() {
        super();
    }

    // Mapping method from Entity to Bean
    public static PartyBean fromEntity(Party entity) {
        if (entity == null) return null;
        PartyBean bean = new PartyBean();
        bean.setId(entity.getId());
        bean.setName(entity.getName());
        bean.setType(entity.getType());
        bean.setAddress(entity.getAddress());
        bean.setDate(entity.getDate());
        bean.setClientName(entity.getClientName());
        bean.setClientPhone(entity.getClientPhone());
        bean.setStartTime(entity.getStartTime());
        bean.setEndTime(entity.getEndTime());
        bean.setChildrenCount(entity.getChildrenCount());
        bean.setAnimatorsRequired(entity.getAnimatorsRequired());
        bean.setDescription(entity.getDescription());
        bean.setCost(entity.getCost());
        bean.setStatus(entity.getStatus());
        bean.setAssignmentStatuses(new java.util.HashMap<>(entity.getAssignmentStatuses())); // Deep copy map
        bean.setAssignmentTimestamps(new java.util.HashMap<>(entity.getAssignmentTimestamps()));
        
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
        entity.setStatus(this.getStatus());
        entity.setAssignmentStatuses(new java.util.HashMap<>(this.getAssignmentStatuses())); // Deep copy map
        entity.setAssignmentTimestamps(new java.util.HashMap<>(this.getAssignmentTimestamps()));
        
        return entity;
    }
}
