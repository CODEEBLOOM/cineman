package com.codebloom.cineman.controller.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MovieVariationRequest {

    @NotBlank(message = "Name's movie variation is must not blank !")
    @NotNull(message = "Name's movie variation is must not null !")
    @Size(min = 1, max = 60, message = "Name's movie variation is must less than or equal 60 character !")
    private String name;
}
