package com.codebloom.cineman.controller.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GenresRequest {

    @NotBlank(message = "Name's genre is not blank !")
    @Size(max = 100, message = "Name's genre is must be less than or equal 100 character !")
    @NotNull(message = "Name's genre is must not null !")
    private String name;

    @NotBlank(message = "Description's genre is not blank !")
    @Size(max = 250, message = "Description's genre is must be less than or equal 250 character !")
    private String description;

}
