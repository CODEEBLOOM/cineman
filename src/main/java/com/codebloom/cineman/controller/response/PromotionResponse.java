package com.codebloom.cineman.controller.response;

import com.codebloom.cineman.common.enums.StatusPromotion;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
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
    private PromotionTypeResponse promotionType;
    private Boolean applicableForAllRanks;
    private List<Integer> membershipRankIds;
    private List<String> membershipRankNames;

}
