package com.codebloom.cineman.controller.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MembershipRankResponse {

    private Integer id;
    private String name;
    private Integer requiredPoint;
    private Double returnPointsTicket;
    private Double returnPointsSnack;
    private Integer priorityLevel;
    private Boolean status;
    private String createdAt;
    private String updatedAt;

}
