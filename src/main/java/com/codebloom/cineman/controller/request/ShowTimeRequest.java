package com.codebloom.cineman.controller.request;

import com.codebloom.cineman.common.enums.ShowTimeStatus;
import com.codebloom.cineman.common.utils.ShowTimeValidation;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalTime;
import java.util.Date;

@Getter
@Setter
@ShowTimeValidation
public class ShowTimeRequest {

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @NotNull(message = "Show date is not null !")
    @FutureOrPresent(message = "Show date must be greater than or equal current date !")
    private Date showDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm:ss")
    @NotNull(message = "Start time is not null !")
    private LocalTime  startTime;

    @NotNull (message = "Origin price is not null !")
    @Min(value = 0, message = "Origin price must be greater than 0 !")
    private Double originPrice;

    private ShowTimeStatus status = ShowTimeStatus.INVALID;

    @NotNull(message = "Id's movie is not null")
    @Min(value = 1, message = "Id's movie is must be greater than 0")
    private Integer movieId;

    @NotNull(message = "Id's cinema theater is not null")
    @Min(value = 1, message = "Id's cinema theater is must be greater than 0")
    private Integer cinemaTheaterId;

}
