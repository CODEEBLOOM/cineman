package com.codebloom.cineman.controller.response;

import com.codebloom.cineman.common.enums.StatusPromotion;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PromotionResponse {
    private Long promotionId;
    private String name;
    private String content;
    private String code;
    private Date startDay;
    private Date endDay;
    private Double discount;
    private String conditionType;
    private String conditionDayOfWeek;
    private Double conditionValue;
    private StatusPromotion status;
}
