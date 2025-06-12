package com.codebloom.cineman.service;

import com.codebloom.cineman.model.MovieDirectorEntity;

public interface MovieDirectorService {

    MovieDirectorEntity addDirectorMovie(Integer movieId, Integer directorId);
    MovieDirectorEntity updateDirectorMovie(Integer id, Integer movieId, Integer directorId);
    void deleteDirectorMovie(Integer movieId, Integer directorId);

}
