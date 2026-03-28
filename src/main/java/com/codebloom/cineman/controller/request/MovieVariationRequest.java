package com.codebloom.cineman.controller.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MovieVariationRequest {

    @NotNull(message = "Name of movie variation must not be null!")
    @NotBlank(message = "Name of movie variation must not be blank!")
    @Size(min = 1, max = 100, message = "Name of movie variation must be less than or equal 100 characters!")
    private String name;
}
