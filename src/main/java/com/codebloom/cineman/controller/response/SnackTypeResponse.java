package com.codebloom.cineman.controller.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SnackTypeResponse {
    private Long id;
    private String name;
    private String description;
    private Boolean isActive;
}
