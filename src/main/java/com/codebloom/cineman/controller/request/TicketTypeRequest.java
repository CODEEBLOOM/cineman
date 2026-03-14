package com.codebloom.cineman.controller.request;

import com.codebloom.cineman.common.enums.TicketType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TicketTypeRequest {

    @NotNull(message = "Name's ticket type is must not null !")
    private TicketType name;

    @Size(max = 200, message = "Description's ticket type is must less than or equal 200 character !")
    private String description;

    @NotNull(message = "Price's ticket type is must not null !")
    @DecimalMin(value = "0.0", inclusive = true, message = "Price's ticket type must be greater than or equal 0 !")
    private Double price;
}
