package com.romanimazione.entity;

public enum Role {
    ANIMATOR,
    ADMIN;

    public static Role fromString(String role) {
        if (role == null) return null;
        try {
            return Role.valueOf(role.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
