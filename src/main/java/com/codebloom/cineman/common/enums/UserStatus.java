package com.codebloom.cineman.common.enums;

import lombok.Getter;
import lombok.Setter;

@Getter
public enum UserStatus {
    NONE(0),
    ACTIVE(1),
    INACTIVE(2),
    PENDING(3);

    private final int value;

    UserStatus(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static UserStatus fromValue(int value) {
        for (UserStatus status : UserStatus.values()) {
            if (status.getValue() == value) {
                return status;
            }
        }
        throw new IllegalArgumentException("Not found: " + value);
    }
}
