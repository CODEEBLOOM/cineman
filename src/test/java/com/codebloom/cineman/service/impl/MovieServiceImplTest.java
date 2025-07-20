package com.codebloom.cineman.service.impl;

import com.codebloom.cineman.common.constant.MovieStatus;
import com.codebloom.cineman.controller.request.MovieCreationRequest;
import com.codebloom.cineman.controller.request.MovieUpdateRequest;
import com.codebloom.cineman.controller.request.MoviePageQueryRequest;
import com.codebloom.cineman.controller.response.MoviePageableResponse;
import com.codebloom.cineman.controller.response.MovieResponse;
import com.codebloom.cineman.exception.DataNotFoundException;
import com.codebloom.cineman.model.*;
import com.codebloom.cineman.repository.MovieRepository;
import com.codebloom.cineman.repository.MovieStatusRepository;
import com.codebloom.cineman.service.MovieStatusService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MovieServiceImplTest {

    @Mock private MovieRepository movieRepository;
    @Mock private MovieStatusRepository movieStatusRepository;
    @Mock private MovieStatusService movieStatusService;
    @Mock private ModelMapper modelMapper;

    @InjectMocks private MovieServiceImpl movieService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void findAllByPage_shouldReturnPagedMovies() {
        MoviePageQueryRequest request = new MoviePageQueryRequest();
        request.setPage(0);
        request.setSize(5);
        request.setStatus(MovieStatus.MOVIE_STATUS_SC);

        MovieStatusEntity statusEntity = MovieStatusEntity.builder()
                .statusId(MovieStatus.MOVIE_STATUS_SC)
                .name("Sắp chiếu")
                .description("desc")
                .active(true)
                .build();

        MovieEntity movie = new MovieEntity();
        movie.setMovieGenres(new HashSet<>());
        movie.setMovieParticipants(new HashSet<>());
        movie.setStatus(statusEntity);

        Page<MovieEntity> page = new PageImpl<>(List.of(movie));

        when(movieStatusRepository.findById(MovieStatus.MOVIE_STATUS_SC)).thenReturn(Optional.of(statusEntity));
        when(movieRepository.findAllByStatus(eq(statusEntity), any(Pageable.class))).thenReturn(page);
        when(modelMapper.map(any(), eq(MovieResponse.class))).thenReturn(new MovieResponse());

        MoviePageableResponse response = movieService.findAllByPage(request);

        assertNotNull(response);
        assertEquals(1, response.getMovies().size());
        verify(movieRepository).findAllByStatus(eq(statusEntity), any(Pageable.class));
    }

    @Test
    void findAll_shouldReturnAllMovies() {
        MovieEntity movie = new MovieEntity();
        movie.setMovieGenres(new HashSet<>());
        movie.setMovieParticipants(new HashSet<>());

        MovieStatusEntity status = new MovieStatusEntity();
        status.setName("Sắp chiếu");
        movie.setStatus(status);

        List<MovieEntity> movies = List.of(movie);
        when(movieRepository.findAll()).thenReturn(movies);
        when(modelMapper.map(any(), eq(MovieResponse.class))).thenReturn(new MovieResponse());

        List<MovieResponse> responses = movieService.findAll();

        assertEquals(1, responses.size());
        verify(movieRepository).findAll();
    }


    @Test
    void findById_shouldReturnMovie_whenFound() {
        MovieEntity movie = new MovieEntity();
        movie.setMovieId(1);
        movie.setStatus(new MovieStatusEntity());
        movie.setMovieGenres(new HashSet<>());
        movie.setMovieParticipants(new HashSet<>());

        when(movieRepository.findById(1)).thenReturn(Optional.of(movie));
        when(modelMapper.map(any(), eq(MovieResponse.class))).thenReturn(new MovieResponse());

        MovieResponse response = movieService.findById(1);

        assertNotNull(response);
        verify(movieRepository).findById(1);
    }

    @Test
    void findById_shouldThrowException_whenNotFound() {
        when(movieRepository.findById(99)).thenReturn(Optional.empty());

        DataNotFoundException exception = assertThrows(DataNotFoundException.class, () -> movieService.findById(99));

        assertEquals("Movie not found with id: 99", exception.getMessage());
    }


    @Test
    void save_shouldCreateNewMovie_whenValid() {
        MovieCreationRequest request = new MovieCreationRequest();
        MovieEntity movie = new MovieEntity();

        when(movieStatusRepository.findById(MovieStatus.MOVIE_STATUS_SC)).thenReturn(Optional.empty());
        when(movieStatusRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        when(modelMapper.map(request, MovieEntity.class)).thenReturn(movie);
        when(movieRepository.save(movie)).thenReturn(movie);

        MovieEntity result = movieService.save(request);

        assertNotNull(result);
        verify(movieRepository).save(movie);
    }

    @Test
    void update_shouldUpdateMovie_whenValid() {
        MovieUpdateRequest request = new MovieUpdateRequest();
        request.setMovieId(1);
        request.setStatus(MovieStatus.MOVIE_STATUS_SC);

        MovieEntity existingMovie = new MovieEntity();
        existingMovie.setCreatedAt(new Date());
        existingMovie.setStatus(new MovieStatusEntity());

        MovieEntity mappedMovie = new MovieEntity();
        mappedMovie.setMovieId(1);
        mappedMovie.setMovieGenres(new HashSet<>());
        mappedMovie.setMovieParticipants(new HashSet<>());
        mappedMovie.setStatus(new MovieStatusEntity());

        when(movieRepository.findById(1)).thenReturn(Optional.of(existingMovie));
        when(movieStatusService.findById(MovieStatus.MOVIE_STATUS_SC)).thenReturn(new MovieStatusEntity());
        when(modelMapper.map(request, MovieEntity.class)).thenReturn(mappedMovie);
        when(movieRepository.save(mappedMovie)).thenReturn(mappedMovie);
        when(modelMapper.map(mappedMovie, MovieResponse.class)).thenReturn(new MovieResponse());

        MovieResponse result = movieService.update(request);

        assertNotNull(result);
        verify(movieRepository).save(mappedMovie);
    }


    @Test
    void delete_shouldUpdateMovieStatusToCancelled() {
        int movieId = 1;
        MovieEntity movie = new MovieEntity();
        movie.setMovieId(movieId);
        movie.setStatus(new MovieStatusEntity());

        when(movieRepository.findById(movieId)).thenReturn(Optional.of(movie));
        when(movieStatusRepository.findById(MovieStatus.MOVIE_STATUS_CNS)).thenReturn(Optional.empty());
        when(movieStatusRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        when(movieRepository.save(movie)).thenReturn(movie);

        movieService.delete(movieId);

        verify(movieRepository).save(movie);
    }

    @Test
    void findByIdEntity_shouldReturnMovie_whenFound() {
        MovieEntity movie = new MovieEntity();
        movie.setMovieId(1);

        when(movieRepository.findById(1)).thenReturn(Optional.of(movie));

        MovieEntity result = movieService.findById(1, true);
        assertEquals(1, result.getMovieId());
    }

    @Test
    void findByIdEntity_shouldThrowException_whenNotFound() {
        when(movieRepository.findById(100)).thenReturn(Optional.empty());

        DataNotFoundException exception = assertThrows(DataNotFoundException.class, () -> movieService.findById(100, true));

        assertEquals("Movie not found with id: 100", exception.getMessage());
    }

}
