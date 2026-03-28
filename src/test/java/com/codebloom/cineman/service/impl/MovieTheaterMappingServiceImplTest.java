package com.codebloom.cineman.service.impl;

import com.codebloom.cineman.common.constant.MovieStatus;
import com.codebloom.cineman.controller.request.MovieTheaterMappingRequest;
import com.codebloom.cineman.controller.response.MovieTheaterMappingResponse;
import com.codebloom.cineman.model.MovieEntity;
import com.codebloom.cineman.model.MovieStatusEntity;
import com.codebloom.cineman.model.MovieTheaterEntity;
import com.codebloom.cineman.model.MovieTheaterMappingEntity;
import com.codebloom.cineman.repository.MovieRepository;
import com.codebloom.cineman.repository.MovieTheaterMappingRepository;
import com.codebloom.cineman.repository.MovieTheaterRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MovieTheaterMappingServiceImplTest {

    @Mock
    private MovieTheaterMappingRepository movieTheaterMappingRepository;

    @Mock
    private MovieRepository movieRepository;

    @Mock
    private MovieTheaterRepository movieTheaterRepository;

    @InjectMocks
    private MovieTheaterMappingServiceImpl movieTheaterMappingService;

    @Test
    void saveShouldCreateActiveMovieTheaterMapping() {
        MovieTheaterMappingRequest request = new MovieTheaterMappingRequest();
        request.setMovieId(10);
        request.setMovieTheaterId(20);

        MovieEntity movie = MovieEntity.builder()
                .movieId(10)
                .title("Movie A")
                .status(MovieStatusEntity.builder().statusId(MovieStatus.MOVIE_STATUS_SC).build())
                .build();
        MovieTheaterEntity movieTheater = MovieTheaterEntity.builder()
                .movieTheaterId(20)
                .name("Rap Ha Noi")
                .status(true)
                .build();

        when(movieTheaterMappingRepository.findByMovie_MovieIdAndMovieTheater_MovieTheaterIdAndActiveTrue(10, 20))
                .thenReturn(Optional.empty());
        when(movieRepository.findById(10)).thenReturn(Optional.of(movie));
        when(movieTheaterRepository.findByMovieTheaterIdAndStatus(20, true)).thenReturn(Optional.of(movieTheater));
        when(movieTheaterMappingRepository.save(any(MovieTheaterMappingEntity.class)))
                .thenAnswer(invocation -> {
                    MovieTheaterMappingEntity entity = invocation.getArgument(0);
                    entity.setMovieTheaterMappingId(1);
                    return entity;
                });

        MovieTheaterMappingResponse response = movieTheaterMappingService.save(request);

        assertEquals(1, response.getMovieTheaterMappingId());
        assertEquals(10, response.getMovieId());
        assertEquals(20, response.getMovieTheaterId());
        assertEquals("Movie A", response.getMovieTitle());
        assertEquals("Rap Ha Noi", response.getMovieTheaterName());
        assertEquals(true, response.getActive());
    }

    @Test
    void updateShouldReplaceMovieAndMovieTheater() {
        MovieTheaterMappingEntity existing = MovieTheaterMappingEntity.builder()
                .movieTheaterMappingId(5)
                .movie(MovieEntity.builder().movieId(1).title("Old").status(MovieStatusEntity.builder().statusId(MovieStatus.MOVIE_STATUS_DB).build()).build())
                .movieTheater(MovieTheaterEntity.builder().movieTheaterId(2).name("Old Theater").status(true).build())
                .active(true)
                .build();
        MovieTheaterMappingRequest request = new MovieTheaterMappingRequest();
        request.setMovieId(11);
        request.setMovieTheaterId(21);

        MovieEntity newMovie = MovieEntity.builder()
                .movieId(11)
                .title("New Movie")
                .status(MovieStatusEntity.builder().statusId(MovieStatus.MOVIE_STATUS_SC).build())
                .build();
        MovieTheaterEntity newMovieTheater = MovieTheaterEntity.builder()
                .movieTheaterId(21)
                .name("New Theater")
                .status(true)
                .build();

        when(movieTheaterMappingRepository.findByMovieTheaterMappingIdAndActiveTrue(5)).thenReturn(Optional.of(existing));
        when(movieTheaterMappingRepository.findByMovie_MovieIdAndMovieTheater_MovieTheaterIdAndActiveTrue(11, 21))
                .thenReturn(Optional.empty());
        when(movieRepository.findById(11)).thenReturn(Optional.of(newMovie));
        when(movieTheaterRepository.findByMovieTheaterIdAndStatus(21, true)).thenReturn(Optional.of(newMovieTheater));
        when(movieTheaterMappingRepository.save(existing)).thenReturn(existing);

        MovieTheaterMappingResponse response = movieTheaterMappingService.update(5, request);

        assertEquals(11, existing.getMovie().getMovieId());
        assertEquals(21, existing.getMovieTheater().getMovieTheaterId());
        assertEquals("New Movie", response.getMovieTitle());
        assertEquals("New Theater", response.getMovieTheaterName());
    }

    @Test
    void deleteShouldSoftDeleteMapping() {
        MovieTheaterMappingEntity existing = MovieTheaterMappingEntity.builder()
                .movieTheaterMappingId(5)
                .active(true)
                .build();

        when(movieTheaterMappingRepository.findByMovieTheaterMappingIdAndActiveTrue(5)).thenReturn(Optional.of(existing));

        movieTheaterMappingService.delete(5);

        assertEquals(false, existing.getActive());
        verify(movieTheaterMappingRepository).save(existing);
    }
}
