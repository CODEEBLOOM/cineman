package com.codebloom.cineman.controller;

import com.codebloom.cineman.controller.response.ApiResponse;
import com.codebloom.cineman.controller.response.MovieTheaterResponse;
import com.codebloom.cineman.service.MovieTheaterService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MovieTheaterControllerTest {

    @Mock
    private MovieTheaterService movieTheaterService;

    @InjectMocks
    private MovieTheaterController movieTheaterController;

    @Test
    void getAllMovieTheatersShouldReturnAllMovieTheatersWithoutPagination() {
        List<MovieTheaterResponse> movieTheaters = List.of(
                MovieTheaterResponse.builder()
                        .movieTheaterId(1)
                        .name("Cineman District 7")
                        .hotline("0909000999")
                        .build()
        );
        when(movieTheaterService.findAll()).thenReturn(movieTheaters);

        ResponseEntity<ApiResponse> response = movieTheaterController.getAllMovieTheaters();

        verify(movieTheaterService).findAll();
        assertEquals(200, response.getStatusCode().value());
        assertEquals("success", response.getBody().getMessage());
        assertEquals(movieTheaters, response.getBody().getData());
    }
}
