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
public class PromotionTypeResponse {

    private Long id;
    private String code;
    private String name;
    private String description;
    private Boolean status;
    private String createdAt;
    private String updatedAt;
}
