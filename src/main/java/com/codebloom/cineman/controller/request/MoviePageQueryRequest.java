package com.codebloom.cineman.controller.request;


import com.codebloom.cineman.common.constant.MovieStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MoviePageQueryRequest {
    private Integer page = 0;
    private Integer size = 12;
    private String status = MovieStatus.MOVIE_STATUS_DC;
}
