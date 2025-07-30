package com.codebloom.cineman.controller.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ApplyPromotionResponse {
    private Long id;
    private String code;
    private Double discount;
}
