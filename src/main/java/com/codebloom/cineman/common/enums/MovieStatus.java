package com.codebloom.cineman.common.enums;


public enum MovieStatus {
    SC("Sắp chiếu"),
    DC("Đang chiếu"),
    NC("Ngưng chiếu"),
    DB("Đặc biệt"),
    CNS("Đã hủy");

    private final String name;

    MovieStatus(String displayName) {
        this.name = displayName;
    }

    public String getDisplayName() {
        return name;
    }
}
