package com.codebloom.cineman.controller.request;

import com.codebloom.cineman.common.constant.MovieTheaterOfficeHours;
import com.codebloom.cineman.common.enums.ShowTimeStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalTime;
import java.util.Date;
import java.util.List;

@Getter
@Setter
public class AutoShowTimeRequest {

    @NotNull(message = "Show date is not null !")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date showDate;

    @NotNull(message = "Id's movie theater is not null")
    @Min(value = 1, message = "Id's movie theater is must be greater than 0")
    private Integer movieTheaterId;

    @NotEmpty(message = "Movie ids is not empty !")
    private List<@NotNull(message = "Movie id is not null !") @Min(value = 1, message = "Movie id must be greater than 0 !") Integer> movieIds;

    @NotNull(message = "Id's movie variation is not null")
    @Min(value = 1, message = "Id's movie variation is must be greater than 0")
    private Integer movieVariationId;

    @NotNull(message = "Origin price is not null !")
    @Min(value = 0, message = "Origin price must be greater than or equal to 0 !")
    private Double originPrice;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm:ss")
    private LocalTime startTime = MovieTheaterOfficeHours.OPENING_HOURS;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm:ss")
    private LocalTime endTime = MovieTheaterOfficeHours.CLOSING_HOURS;

    @Min(value = 0, message = "Buffer minutes must be greater than or equal to 0 !")
    private Integer bufferMinutes = 15;

    private ShowTimeStatus status = ShowTimeStatus.INVALID;

    private Boolean special = false;
}
