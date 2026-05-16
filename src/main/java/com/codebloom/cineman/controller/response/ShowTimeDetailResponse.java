package com.codebloom.cineman.controller.response;

import com.codebloom.cineman.model.CinemaTheaterEntity;
import com.codebloom.cineman.model.MovieEntity;
import com.codebloom.cineman.model.MovieVariationEntity;
import com.codebloom.cineman.model.ShowTimeEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShowTimeDetailResponse {

    private ShowTimeEntity showTime;
    private Integer totalSeatEmpty;
    private MovieEntity movie;
    private CinemaTheaterEntity cinemaTheater;
    private MovieVariationEntity movieVariation;

}
