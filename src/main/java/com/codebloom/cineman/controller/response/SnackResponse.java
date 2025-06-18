package com.codebloom.cineman.controller.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SnackResponse {
    private Integer id;
    private String snackName;
    private Double unitPrice;
    private String image;
    private String description;
    private String snackTypeName;
    private Boolean isActive;
}