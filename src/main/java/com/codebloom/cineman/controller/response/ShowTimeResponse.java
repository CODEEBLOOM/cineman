package com.codebloom.cineman.controller.response;

import com.codebloom.cineman.common.enums.ShowTimeStatus;
import com.codebloom.cineman.model.CinemaTheaterEntity;
import com.codebloom.cineman.model.MovieEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalTime;
import java.util.Date;

@Getter
@Builder
public class ShowTimeResponse {

    private Long id;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm:ss")
    private LocalTime startTime;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm:ss")
    private LocalTime endTime;

    private Date showDate;
    private Double originPrice;
    private MovieEntity movie;
    private CinemaTheaterEntity cinemaTheater;
    private ShowTimeStatus status;

}
