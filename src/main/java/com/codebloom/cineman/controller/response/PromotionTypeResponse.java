package com.codebloom.cineman.controller.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class PromotionTypeResponse {

    private Long id;
    private String code;
    private String name;
    private String description;
    private Boolean status;
    private String createdAt;
    private String updatedAt;
}
