package com.codebloom.cineman.service;


import com.codebloom.cineman.controller.request.*;
import com.codebloom.cineman.controller.response.MoviePageableResponse;
import com.codebloom.cineman.controller.response.MovieResponse;
import com.codebloom.cineman.model.MovieEntity;

import java.util.List;


public interface MovieService  {

    MoviePageableResponse findAllByPage(MoviePageQueryRequest request);
    MoviePageableResponse findAllByPageAndFilter(MoviePageQueryRequest request, Integer movieTheaterId);
    List<MovieResponse> findAll();
    MovieResponse findById(Integer id);
    MovieEntity findById(Integer id, boolean isEntity);
    MovieEntity save(MovieCreationRequest movie);
    MovieResponse update(MovieUpdateRequest movie);
    void delete(Integer id );


}
