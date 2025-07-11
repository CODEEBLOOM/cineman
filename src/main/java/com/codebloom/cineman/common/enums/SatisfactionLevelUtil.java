package com.codebloom.cineman.common.enums;

import java.util.Map;

public class SatisfactionLevelUtil {
    private static final Map<SatisfactionLevel, String> VIETNAMESE_LABELS = Map.of(
            SatisfactionLevel.VERY_SATISFIED, "Rất hài lòng",
            SatisfactionLevel.SATISFIED, "Hài lòng",
            SatisfactionLevel.NEUTRAL, "Bình thường",
            SatisfactionLevel.DISSATISFIED, "Không hài lòng",
            SatisfactionLevel.VERY_DISSATISFIED, "Rất không hài lòng"
    );

    public static String getLabel(SatisfactionLevel level) {
        return VIETNAMESE_LABELS.getOrDefault(level, "Không xác định");
    }
}
