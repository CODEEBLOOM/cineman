package com.codebloom.cineman.common.enums;

public enum GenderUser {

    FEMALE(0),
    MALE(1),
    OTHER(2);

    private int value;
    private GenderUser(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

}
