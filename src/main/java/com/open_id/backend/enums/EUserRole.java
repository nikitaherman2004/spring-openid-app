package com.open_id.backend.enums;

public enum EUserRole {
    USER("user"),
    ADMIN("admin");

    private final String value;

    EUserRole(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
