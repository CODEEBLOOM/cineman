package com.codebloom.cineman.controller.response;

import com.codebloom.cineman.model.SnackTypeEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

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
    private Boolean isActive;
    private SnackTypeEntity snackTypes;
}