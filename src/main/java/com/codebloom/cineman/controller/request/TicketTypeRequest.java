package com.codebloom.cineman.controller.request;

import com.codebloom.cineman.common.enums.TicketType;
import com.codebloom.cineman.common.utils.EnumPattern;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TicketTypeRequest {

    @NotNull(message = "Name of ticket type must not be null!")
    @EnumPattern(name = "name", regexp = "ADULT|CHILD|STUDENT|SENIOR")
    private TicketType name;

    @NotBlank(message = "Description of ticket type must not be blank!")
    @Size(max = 200, message = "Description of ticket type must be less than or equal 200 characters!")
    private String description;

    @NotNull(message = "Price of ticket type must not be null!")
    @Min(value = 0, message = "Price of ticket type must be greater than or equal 0!")
    private Double price;
}
