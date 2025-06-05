package com.codebloom.cineman.common.enums;

public enum Rating {
    BAD(1),
    AVERAGE(2),
    GOOD(3),
    VERY_GOOD(4),
    EXCELLENT(5);

    private final int value;

    Rating (int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
