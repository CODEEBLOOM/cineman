package com.codebloom.cineman.controller.response;

import com.codebloom.cineman.common.enums.StatusPromotion;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class PromotionResponse {

    private Long id;
    private String name;
    private String content;
    private String code;
    private String startDate;
    private String endDate;
    private Double discount;
    private Integer quantity;
    private Double limitAmount;
    private StatusPromotion status;

}
