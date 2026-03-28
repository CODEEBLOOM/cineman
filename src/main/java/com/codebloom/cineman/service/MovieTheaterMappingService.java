package com.codebloom.cineman.service;

import com.codebloom.cineman.controller.request.MovieTheaterMappingRequest;
import com.codebloom.cineman.controller.request.PageRequest;
import com.codebloom.cineman.controller.response.MovieTheaterMappingPage;
import com.codebloom.cineman.controller.response.MovieTheaterMappingResponse;

import java.util.List;

public interface MovieTheaterMappingService {

    MovieTheaterMappingPage findAllByPage(PageRequest pageRequest);

    List<MovieTheaterMappingResponse> findAllByMovieId(Integer movieId);

    List<MovieTheaterMappingResponse> findAllByMovieTheaterId(Integer movieTheaterId);

    MovieTheaterMappingResponse findById(Integer id);

    MovieTheaterMappingResponse save(MovieTheaterMappingRequest request);

    MovieTheaterMappingResponse update(Integer id, MovieTheaterMappingRequest request);

    void delete(Integer id);
}
