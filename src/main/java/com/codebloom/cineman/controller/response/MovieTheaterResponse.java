package com.codebloom.cineman.controller.response;

import com.codebloom.cineman.model.CinemaTheaterEntity;
import com.codebloom.cineman.model.ProvinceEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class MovieTheaterResponse {

    private Integer movieTheaterId;
    private String name;
    private String address;
    private Integer numbersOfCinemaTheater;
    private String hotline;
    private Boolean status;
    private String iframeCode;
    private ProvinceEntity province;
    private List<CinemaTheaterEntity> cinemaTheaters;

}
