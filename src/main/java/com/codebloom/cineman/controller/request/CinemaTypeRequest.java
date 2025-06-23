package com.codebloom.cineman.controller.request;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CinemaTypeRequest {

    @NotNull(message = "Code's cinema type is must not null !")
    @Size(min = 1, max = 25, message = "Code's cinema type is must less than or equal 25 character !")
    String code;

    @NotBlank(message = "Name's cinema type is must not blank !")
    @Size(min = 1, max = 200, message = "Name's cinema type is must less than or equal 200 character !")
    String name;

    @NotBlank(message = "Description's cinema type is must not blank !")
    @Size(min = 1, max = 250, message = "Description's cinema type is must less than or equal 250 character !")
    String description;

}
