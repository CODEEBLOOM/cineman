package com.codebloom.cineman.controller.response;

import com.codebloom.cineman.common.enums.ShowTimeStatus;
import com.codebloom.cineman.model.CinemaTheaterEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalTime;
import java.util.Date;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShowTimeResponse {

    private Long id;
    private LocalTime startTime;
    private LocalTime endTime;
    private Date showDate;
    private Double originPrice;
    private MovieResponse movie;
    private CinemaTheaterEntity cinemaTheater;
    private ShowTimeStatus status;
    private Boolean special;

}
