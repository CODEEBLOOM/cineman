package com.codebloom.cineman.controller.response;

import com.codebloom.cineman.model.CinemaTheaterEntity;
import com.codebloom.cineman.model.MovieEntity;
import com.codebloom.cineman.model.ShowTimeEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ShowTimeDetailResponse {

    private ShowTimeEntity showTime;
    private Integer totalSeatEmpty;
    private MovieEntity movie;
    private CinemaTheaterEntity cinemaTheater;

}
