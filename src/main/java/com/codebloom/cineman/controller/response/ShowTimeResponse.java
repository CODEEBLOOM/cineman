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
    private LocalTime startTime;
    private LocalTime endTime;
    private Date showDate;
    private Double originPrice;
    private MovieResponse movie;
    private CinemaTheaterEntity cinemaTheater;
    private ShowTimeStatus status;

}
