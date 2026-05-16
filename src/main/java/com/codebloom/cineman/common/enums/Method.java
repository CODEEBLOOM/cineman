package com.codebloom.cineman.common.enums;

import java.util.Arrays;

public enum Method {
    GET(0),
    POST(1),
    PUT(2),
    PATCH(3),
    DELETE(4),
    OPTIONS(5);

    private final int code;

    Method(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public static Method fromCode(Integer code) {
        if (code == null) {
            return null;
        }

        return Arrays.stream(values())
                .filter(method -> method.code == code)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unsupported method code: " + code));
    }
}
