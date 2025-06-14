package com.codebloom.cineman.controller.response;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class MoviePageableResponse {

    private List<MovieResponse> movies;
    private MetaResponse meta;

}
