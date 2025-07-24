package com.codebloom.cineman.service.impl;

import com.codebloom.cineman.controller.request.MovieGenreRequest;
import com.codebloom.cineman.exception.*;
import com.codebloom.cineman.model.GenresEntity;
import com.codebloom.cineman.model.MovieEntity;
import com.codebloom.cineman.model.MovieGenresEntity;
import com.codebloom.cineman.repository.MovieGenresRepository;
import com.codebloom.cineman.service.GenreService;
import com.codebloom.cineman.service.MovieGenreService;
import com.codebloom.cineman.service.MovieService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MovieGenreServiceImpl implements MovieGenreService {

    private final MovieGenresRepository movieGenresRepository;
    private final MovieService movieService;
    private final GenreService genreService;


    @Override
    public MovieGenresEntity addMovieGenre(MovieGenreRequest request) {
        MovieEntity movieEntity = movieService.findById(request.getMovieId(), true);
        GenresEntity genresEntity = genreService.findById(request.getGenreId());

        MovieGenresEntity movieGenresEntity = new MovieGenresEntity();
        movieGenresEntity.setGenres(genresEntity);
        movieGenresEntity.setMovie(movieEntity);

        return movieGenresRepository.save(movieGenresEntity);
    }

    @Override
    public MovieGenresEntity updateGenreOfMovie(MovieGenreRequest request, Integer movieIdUpdate) {
        MovieEntity movieEntity = movieService.findById(request.getMovieId(), true);
        GenresEntity genresEntity = genreService.findById(request.getGenreId());

        MovieGenresEntity existingMovieGenre = movieGenresRepository.findByMovieAndGenres(movieEntity, genresEntity)
                .orElseThrow(() -> new DataNotFoundException("movie genre not found with id " + request.getMovieId() + " and genre " + request.getGenreId()));

        existingMovieGenre.setGenres(genreService.findById(movieIdUpdate));
        return movieGenresRepository.save(existingMovieGenre);
    }

    @Override
    public void deleteMovieGenre(MovieGenreRequest request) {
        MovieEntity movieEntity = movieService.findById(request.getMovieId(), true);
        GenresEntity genresEntity = genreService.findById(request.getGenreId());
        movieGenresRepository.findByMovieAndGenres(movieEntity, genresEntity)
                .ifPresent(movieGenresRepository::delete);
    }

}
