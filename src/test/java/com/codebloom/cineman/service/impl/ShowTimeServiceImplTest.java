package com.codebloom.cineman.service.impl;

import com.codebloom.cineman.common.enums.ShowTimeStatus;
import com.codebloom.cineman.controller.request.MoviePageQueryRequest;
import com.codebloom.cineman.controller.request.ShowTimeRequestNew;
import com.codebloom.cineman.controller.response.MoviePageableResponse;
import com.codebloom.cineman.controller.response.MovieResponse;
import com.codebloom.cineman.model.MovieEntity;
import com.codebloom.cineman.repository.CinemaTheatersRepository;
import com.codebloom.cineman.repository.MovieRepository;
import com.codebloom.cineman.repository.MovieVariationRepository;
import com.codebloom.cineman.repository.SeatRepository;
import com.codebloom.cineman.repository.ShowTimeRepository;
import com.codebloom.cineman.service.MovieService;
import com.codebloom.cineman.service.MovieStatusService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Collections;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ShowTimeServiceImplTest {

    @Mock
    private ShowTimeRepository showTimeRepository;

    @Mock
    private CinemaTheatersRepository cinemaTheaterRepository;

    @Mock
    private MovieRepository movieRepository;

    @Mock
    private MovieService movieService;

    @Mock
    private MovieStatusService movieStatusService;

    @Mock
    private MovieVariationRepository movieVariationRepository;

    @Mock
    private SeatRepository seatRepository;

    @InjectMocks
    private ShowTimeServiceImpl showTimeService;

    @Test
    void findAllByFilter_shouldUseSpecificationQueryForNullableFilters() {
        ShowTimeRequestNew request = new ShowTimeRequestNew();
        request.setMovieTheaterId(1L);
        request.setShowTimeStatus(ShowTimeStatus.VALID);
        request.setShowDate(Date.from(LocalDate.of(2026, 3, 14)
                .atStartOfDay(ZoneId.systemDefault())
                .toInstant()));

        when(showTimeRepository.findAll(
                any(Specification.class),
                eq(Sort.by(Sort.Order.desc("showDate"), Sort.Order.asc("startTime")))
        )).thenReturn(Collections.emptyList());

        assertNull(showTimeService.findAllByFilter(request));

        verify(showTimeRepository).findAll(
                any(Specification.class),
                eq(Sort.by(Sort.Order.desc("showDate"), Sort.Order.asc("startTime")))
        );
        verify(showTimeRepository, never()).findAllByFilter(any(), any(), any());
    }

    @Test
    void findAllShowDateByCinemaTheaterIdInFeatured_shouldFallbackToMovieTheaterId() {
        Sort sort = Sort.by(Sort.Direction.ASC, "showDate");
        Date showDate = Date.from(LocalDate.of(2026, 4, 26)
                .atStartOfDay(ZoneId.systemDefault())
                .toInstant());

        when(showTimeRepository.findAllShowDateByCinemaTheaterIdAndStatusInFeatured(1, ShowTimeStatus.VALID, sort))
                .thenReturn(Collections.emptyList());
        when(showTimeRepository.findAllShowDateByMovieTheaterIdAndStatusInFeatured(1, ShowTimeStatus.VALID, sort))
                .thenReturn(List.of(showDate));

        List<Date> result = showTimeService.findAllShowDateByCinemaTheaterIdInFeatured(1);

        assertEquals(List.of(showDate), result);
        verify(showTimeRepository).findAllShowDateByCinemaTheaterIdAndStatusInFeatured(1, ShowTimeStatus.VALID, sort);
        verify(showTimeRepository).findAllShowDateByMovieTheaterIdAndStatusInFeatured(1, ShowTimeStatus.VALID, sort);
    }

    @Test
    void findAllMovieByCinemaTheaterIdAndShowDate_shouldFallbackToMovieTheaterId() {
        Date showDate = Date.from(LocalDate.of(2026, 4, 26)
                .atStartOfDay(ZoneId.systemDefault())
                .toInstant());
        MoviePageQueryRequest request = new MoviePageQueryRequest();
        request.setPage(0);
        request.setSize(10);

        Pageable pageable = PageRequest.of(0, 10);
        Page<MovieEntity> emptyPage = Page.empty(pageable);
        MovieEntity movieEntity = new MovieEntity();
        Page<MovieEntity> fallbackPage = new PageImpl<>(List.of(movieEntity), pageable, 1);
        MovieResponse movieResponse = MovieResponse.builder().movieId(3).title("fallback").build();
        MoviePageableResponse pageableResponse = new MoviePageableResponse();
        pageableResponse.setMovies(List.of(movieResponse));

        when(movieRepository.findAllMovieByCinemaTheaterIdAndShowDate(1, ShowTimeStatus.VALID, showDate, pageable))
                .thenReturn(emptyPage);
        when(movieRepository.findAllMovieByMovieTheaterIdAndShowDate(1, ShowTimeStatus.VALID, showDate, pageable))
                .thenReturn(fallbackPage);
        when(movieService.movieToMoviePageableResponse(fallbackPage)).thenReturn(pageableResponse);

        List<MovieResponse> result = showTimeService.findAllMovieByCinemaTheaterIdAndShowDate(1, showDate, request);

        assertEquals(List.of(movieResponse), result);
        verify(movieRepository).findAllMovieByCinemaTheaterIdAndShowDate(1, ShowTimeStatus.VALID, showDate, pageable);
        verify(movieRepository).findAllMovieByMovieTheaterIdAndShowDate(1, ShowTimeStatus.VALID, showDate, pageable);
    }
}
