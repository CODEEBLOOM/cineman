package com.codebloom.cineman.service;

import com.codebloom.cineman.controller.request.MovieGenreRequest;
import com.codebloom.cineman.model.MovieGenresEntity;

public interface MovieGenreService {

    MovieGenresEntity addMovieGenre(MovieGenreRequest request);
    MovieGenresEntity updateGenreOfMovie(MovieGenreRequest request, Integer movieIdUpdate);

    void deleteMovieGenre(MovieGenreRequest request);



}
