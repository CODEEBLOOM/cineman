package com.codebloom.cineman.controller.request;

import com.codebloom.cineman.model.CinemaTheaterEntity;
import com.codebloom.cineman.model.MovieEntity;
import com.codebloom.cineman.model.MovieTheaterEntity;
import com.codebloom.cineman.model.ShowTimeEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class ShowTimeDetailResponseNew {

    private MovieEntity movie;
    private CinemaTheaterEntity cinemaTheater;
    private MovieTheaterEntity movieTheater;
    private List<ShowTimeEntity> showTimes;

}
