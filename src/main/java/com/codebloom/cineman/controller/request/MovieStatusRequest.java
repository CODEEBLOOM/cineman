package com.codebloom.cineman.controller.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MovieStatusRequest {

    @NotBlank(message = "Id of movie status is not blank !")
    @Size(max = 25, message = "Id of movie status must be less than 16 character !")
    @NotNull(message = "Id of movie status is not null !")
    private String id;

    @NotBlank(message = "Name of movie status is not blank !")
    @Size(min  = 1, max = 100, message = "Name of movie status must be less than 100 character !")
    @NotNull(message = "Name of movie status is not null !")
    private String name;

    @NotBlank(message = "Name of movie status is not blank !")
    @Size(min  = 1, max = 250, message = "Description of movie status must be less than 250 character !")
    private String description;
    
}
