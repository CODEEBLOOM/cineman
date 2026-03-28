package com.codebloom.cineman.controller.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MovieTheaterMappingRequest {

    @NotNull(message = "Id's movie is not null")
    @Min(value = 1, message = "Id's movie is must be greater than 0")
    private Integer movieId;

    @NotNull(message = "Id's movie theater is not null")
    @Min(value = 1, message = "Id's movie theater is must be greater than 0")
    private Integer movieTheaterId;
}
