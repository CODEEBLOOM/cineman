package com.codebloom.cineman.controller.admin;

import com.codebloom.cineman.controller.response.MovieTheaterResponse;
import com.codebloom.cineman.model.ProvinceEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@Builder
public class MovieTheaterProvinceResponse {

    private ProvinceEntity province;
    private List<MovieTheaterResponse> movieTheaters;

}
