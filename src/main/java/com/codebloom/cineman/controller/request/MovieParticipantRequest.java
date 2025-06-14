package com.codebloom.cineman.controller.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MovieParticipantRequest {

    @NotNull(message = "Id's movie is not null")
    @Min(value = 1, message = "Id's movie is must be greater than 0")
    private Integer movieId;

    @NotNull(message = "Id's movie is not null")
    @Min(value = 1, message = "Id's participant is must be greater than 0")
    private Integer participantId;

    @NotNull(message = "Id's movie role is not null")
    @Min(value = 1, message = "Id's movie role is must be greater than 0")
    private Integer movieRoleId;

}
