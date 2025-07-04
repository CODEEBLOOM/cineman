package com.codebloom.cineman.controller.request;

import com.codebloom.cineman.common.enums.SeatType;
import com.codebloom.cineman.common.utils.EnumPattern;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SeatTypeRequest {

    @NotNull(message = "Id of seat type is must not null !")
    @EnumPattern(name = "id", regexp = "VIP|REGULAR|DOUBLE")
    private SeatType id;

    @NotNull(message = "Name of seat type is must not null !")
    @NotBlank(message = "Name of seat type is must not blank !")
    @Size(min = 1, max = 200, message = "Name of seat type is must be less than or equal 200 character !")
    private String name;

    @Min(value = 0, message = "Price of seat type must be greater than 0 !")
    @NotNull(message = "Price of seat type is must not null !")
    private Double price;

}
