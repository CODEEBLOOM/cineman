package com.codebloom.cineman.common.enums;


public enum MovieStatus {
    COMING_SOON("Sắp chiếu"),
    NOW_SHOWING("Đang chiếu"),
    ENDED("Đã chiếu"),
    CANCELLED("Đã hủy"),
    POSTPONED("Hoãn chiếu");

    private final String displayName;

    MovieStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
