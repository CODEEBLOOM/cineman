package com.codebloom.cineman.service.impl;

import com.codebloom.cineman.controller.request.MovieGenreRequest;
import com.codebloom.cineman.exception.DataNotFoundException;
import com.codebloom.cineman.model.GenresEntity;
import com.codebloom.cineman.model.MovieEntity;
import com.codebloom.cineman.model.MovieGenresEntity;
import com.codebloom.cineman.repository.MovieGenresRepository;
import com.codebloom.cineman.service.GenreService;
import com.codebloom.cineman.service.MovieService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MovieGenreServiceImplTest {

    @Mock private MovieGenresRepository movieGenresRepository;
    @Mock private MovieService movieService;
    @Mock private GenreService genreService;

    @InjectMocks private MovieGenreServiceImpl movieGenreService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void addMovieGenre_shouldSaveAndReturnMovieGenre() {

        MovieGenreRequest request = new MovieGenreRequest();
        request.setMovieId(1);
        request.setGenreId(2);

        MovieEntity movie = new MovieEntity();
        movie.setMovieId(1);

        GenresEntity genre = new GenresEntity();
        genre.setGenresId(2);

        MovieGenresEntity movieGenresEntity = new MovieGenresEntity();
        movieGenresEntity.setMovie(movie);
        movieGenresEntity.setGenres(genre);

        when(movieService.findById(1, true)).thenReturn(movie);
        when(genreService.findById(2)).thenReturn(genre);
        when(movieGenresRepository.save(any(MovieGenresEntity.class))).thenReturn(movieGenresEntity);

        MovieGenresEntity result = movieGenreService.addMovieGenre(request);

        assertNotNull(result);
        assertEquals(1, result.getMovie().getMovieId());
        assertEquals(2, result.getGenres().getGenresId());
        verify(movieGenresRepository).save(any(MovieGenresEntity.class));
    }

    @Test
    void updateGenreOfMovie_shouldUpdateGenreAndSave() {

        MovieGenreRequest request = new MovieGenreRequest();
        request.setMovieId(1);
        request.setGenreId(2);

        int movieIdUpdate = 3;

        MovieEntity movie = new MovieEntity();
        movie.setMovieId(1);

        GenresEntity oldGenre = new GenresEntity();
        oldGenre.setGenresId(2);

        GenresEntity newGenre = new GenresEntity();
        newGenre.setGenresId(3);

        MovieGenresEntity existing = new MovieGenresEntity();
        existing.setMovie(movie);
        existing.setGenres(oldGenre);

        when(movieService.findById(1, true)).thenReturn(movie);
        when(genreService.findById(2)).thenReturn(oldGenre);
        when(movieGenresRepository.findByMovieAndGenres(movie, oldGenre)).thenReturn(Optional.of(existing));
        when(genreService.findById(3)).thenReturn(newGenre);
        when(movieGenresRepository.save(existing)).thenReturn(existing);

        MovieGenresEntity result = movieGenreService.updateGenreOfMovie(request, movieIdUpdate);

        assertNotNull(result);
        assertEquals(3, result.getGenres().getGenresId());
        verify(movieGenresRepository).save(existing);
    }

    @Test
    void updateGenreOfMovie_shouldThrowException_whenNotFound() {
        MovieGenreRequest request = new MovieGenreRequest();
        request.setMovieId(1);
        request.setGenreId(2);

        MovieEntity movie = new MovieEntity();
        GenresEntity genre = new GenresEntity();

        when(movieService.findById(1, true)).thenReturn(movie);
        when(genreService.findById(2)).thenReturn(genre);
        when(movieGenresRepository.findByMovieAndGenres(movie, genre)).thenReturn(Optional.empty());

        DataNotFoundException ex = assertThrows(DataNotFoundException.class, () ->
                movieGenreService.updateGenreOfMovie(request, 3));

        assertEquals("movie genre not found with id 1 and genre 2", ex.getMessage());
    }


    @Test
    void deleteMovieGenre_shouldDeleteWhenFound() {

        MovieGenreRequest request = new MovieGenreRequest();
        request.setMovieId(1);
        request.setGenreId(2);

        MovieEntity movie = new MovieEntity();
        GenresEntity genre = new GenresEntity();

        MovieGenresEntity movieGenresEntity = new MovieGenresEntity();

        when(movieService.findById(1, true)).thenReturn(movie);
        when(genreService.findById(2)).thenReturn(genre);
        when(movieGenresRepository.findByMovieAndGenres(movie, genre)).thenReturn(Optional.of(movieGenresEntity));

        movieGenreService.deleteMovieGenre(request);

        verify(movieGenresRepository).delete(movieGenresEntity);
    }

    @Test
    void deleteMovieGenre_shouldDoNothingWhenNotFound() {

        MovieGenreRequest request = new MovieGenreRequest();
        request.setMovieId(1);
        request.setGenreId(2);

        MovieEntity movie = new MovieEntity();
        GenresEntity genre = new GenresEntity();

        when(movieService.findById(1, true)).thenReturn(movie);
        when(genreService.findById(2)).thenReturn(genre);
        when(movieGenresRepository.findByMovieAndGenres(movie, genre)).thenReturn(Optional.empty());

        movieGenreService.deleteMovieGenre(request);

        verify(movieGenresRepository, never()).delete(any());
    }
}

