package com.codebloom.cineman.common.enums;

public enum UserType {
    ADMIN(0),
    STAFF(1),
    USER(2);

    private final int value;
    UserType(final int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
