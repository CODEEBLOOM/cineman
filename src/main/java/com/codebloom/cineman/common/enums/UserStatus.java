package com.codebloom.cineman.common.enums;

public enum UserStatus {

    NONE(0),
    ACTIVE(1),
    INACTIVE(2);

    private final int value;

    UserStatus(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

}
