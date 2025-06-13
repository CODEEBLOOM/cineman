package com.codebloom.cineman.controller.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MovieRoleRequest {

    @NotBlank(message = "Name of movie role is not blank !")
    @Size(max = 100, message = "Name of movie role must be less than or equal 100 character !")
    private String name;

    @NotBlank(message = "Description of movie role is not blank !")
    @Size(max = 250, message = "Name of movie role must be less than or equal 250 character !")
    private String description;
}
