package com.codebloom.cineman.common.enums;

public enum SatisfactionLevel {
    VERY_SATISFIED(0), // Rất hài lòng
    SATISFIED(1), // Hài lòng
    NEUTRAL(2), // Bình thường
    DISSATISFIED(3), // Không hài lòng
    VERY_DISSATISFIED(4); // Rất không hài lòng

    private final int description;

    SatisfactionLevel(int description) {
        this.description = description;
    }

    public int getDescription() {
        return description;
    }
}
