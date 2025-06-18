package com.codebloom.cineman.common.enums;

public enum UserType {
    ADMIN("Administrator"),
    CADMIN("Cinema Admin"),
    GUEST("Guest"),
    RCP("Receptionist"),
    USER("User");

    private final String displayName;

    UserType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
