package com.codebloom.cineman.service.impl;

import com.codebloom.cineman.common.constant.MovieStatus;
import com.codebloom.cineman.common.enums.ShowTimeStatus;
import com.codebloom.cineman.controller.request.MoviePageQueryRequest;
import com.codebloom.cineman.controller.request.ShowTimeRequest;
import com.codebloom.cineman.controller.request.ShowTimeRequestNew;
import com.codebloom.cineman.controller.response.MoviePageableResponse;
import com.codebloom.cineman.controller.response.MovieResponse;
import com.codebloom.cineman.model.CinemaTheaterEntity;
import com.codebloom.cineman.model.MovieEntity;
import com.codebloom.cineman.model.MovieStatusEntity;
import com.codebloom.cineman.model.MovieVariationEntity;
import com.codebloom.cineman.model.ShowTimeEntity;
import com.codebloom.cineman.repository.CinemaTheatersRepository;
import com.codebloom.cineman.repository.MovieRepository;
import com.codebloom.cineman.repository.MovieVariationRepository;
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

import java.time.LocalTime;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
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
        verify(showTimeRepository, never()).findAllByFilter(anyInt(), any(), any(), any(Sort.class));
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

    @Test
    void create_shouldSetMovieStatusToDcWhenMovieHasAvailableShowTime() {
        Date tomorrow = daysFromToday(1);
        MovieStatusEntity scStatus = movieStatus(MovieStatus.MOVIE_STATUS_SC);
        MovieStatusEntity dcStatus = movieStatus(MovieStatus.MOVIE_STATUS_DC);
        MovieEntity movie = movie(10, scStatus, daysFromToday(10), daysFromToday(-5));
        CinemaTheaterEntity cinemaTheater = CinemaTheaterEntity.builder().cinemaTheaterId(2).build();
        MovieVariationEntity movieVariation = MovieVariationEntity.builder().id(3).build();

        ShowTimeRequest request = new ShowTimeRequest();
        request.setMovieId(10);
        request.setCinemaTheaterId(2);
        request.setMovieVariationId(3);
        request.setShowDate(tomorrow);
        request.setStartTime(LocalTime.of(10, 0));
        request.setOriginPrice(100_000D);
        request.setStatus(ShowTimeStatus.VALID);
        request.setSpecial(false);

        when(cinemaTheaterRepository.findByStatusAndCinemaTheaterId(any(), eq(2))).thenReturn(Optional.of(cinemaTheater));
        when(movieRepository.findById(10)).thenReturn(Optional.of(movie));
        when(movieVariationRepository.findById(3)).thenReturn(Optional.of(movieVariation));
        when(showTimeRepository.findAllByCinemaTheaterAndShowDateAndStatusNot(eq(cinemaTheater), eq(tomorrow), eq(ShowTimeStatus.DELETED), any(Sort.class)))
                .thenReturn(Collections.emptyList());
        when(showTimeRepository.save(any(ShowTimeEntity.class))).thenAnswer(invocation -> {
            ShowTimeEntity entity = invocation.getArgument(0);
            entity.setId(99L);
            return entity;
        });
        when(showTimeRepository.findAllByMovieAndStatusNot(movie, ShowTimeStatus.DELETED))
                .thenAnswer(invocation -> List.of(savedShowTime(movie, cinemaTheater, tomorrow, LocalTime.of(10, 0), LocalTime.of(12, 0), ShowTimeStatus.VALID)));
        when(movieStatusService.findById(MovieStatus.MOVIE_STATUS_DC)).thenReturn(dcStatus);
        when(movieService.findById(10)).thenReturn(MovieResponse.builder().movieId(10).title("Movie").build());

        showTimeService.create(request);

        assertEquals(MovieStatus.MOVIE_STATUS_DC, movie.getStatus().getStatusId());
    }

    @Test
    void delete_shouldSetMovieStatusToScWhenNoAvailableShowTimeAndMovieNotEnded() {
        MovieStatusEntity dcStatus = movieStatus(MovieStatus.MOVIE_STATUS_DC);
        MovieStatusEntity scStatus = movieStatus(MovieStatus.MOVIE_STATUS_SC);
        MovieEntity movie = movie(20, dcStatus, daysFromToday(5), daysFromToday(-10));
        ShowTimeEntity showTime = ShowTimeEntity.builder()
                .id(7L)
                .movie(movie)
                .status(ShowTimeStatus.VALID)
                .build();

        when(showTimeRepository.findByIdAndStatusNot(7L, ShowTimeStatus.DELETED)).thenReturn(Optional.of(showTime));
        when(showTimeRepository.save(showTime)).thenReturn(showTime);
        when(movieRepository.findById(20)).thenReturn(Optional.of(movie));
        when(showTimeRepository.findAllByMovieAndStatusNot(movie, ShowTimeStatus.DELETED)).thenReturn(Collections.emptyList());
        when(movieStatusService.findById(MovieStatus.MOVIE_STATUS_SC)).thenReturn(scStatus);

        showTimeService.delete(7L);

        assertEquals(ShowTimeStatus.DELETED, showTime.getStatus());
        assertEquals(MovieStatus.MOVIE_STATUS_SC, movie.getStatus().getStatusId());
    }

    @Test
    void delete_shouldSetMovieStatusToNcWhenNoAvailableShowTimeAndMovieEnded() {
        MovieStatusEntity dcStatus = movieStatus(MovieStatus.MOVIE_STATUS_DC);
        MovieStatusEntity ncStatus = movieStatus(MovieStatus.MOVIE_STATUS_NC);
        MovieEntity movie = movie(21, dcStatus, daysFromToday(-1), daysFromToday(-20));
        ShowTimeEntity showTime = ShowTimeEntity.builder()
                .id(8L)
                .movie(movie)
                .status(ShowTimeStatus.VALID)
                .build();

        when(showTimeRepository.findByIdAndStatusNot(8L, ShowTimeStatus.DELETED)).thenReturn(Optional.of(showTime));
        when(showTimeRepository.save(showTime)).thenReturn(showTime);
        when(movieRepository.findById(21)).thenReturn(Optional.of(movie));
        when(showTimeRepository.findAllByMovieAndStatusNot(movie, ShowTimeStatus.DELETED)).thenReturn(Collections.emptyList());
        when(movieStatusService.findById(MovieStatus.MOVIE_STATUS_NC)).thenReturn(ncStatus);

        showTimeService.delete(8L);

        assertEquals(MovieStatus.MOVIE_STATUS_NC, movie.getStatus().getStatusId());
    }

    @Test
    void update_shouldRefreshStatusesForOldAndNewMoviesWhenMovieChanges() {
        Date tomorrow = daysFromToday(1);
        MovieStatusEntity scStatus = movieStatus(MovieStatus.MOVIE_STATUS_SC);
        MovieStatusEntity dcStatus = movieStatus(MovieStatus.MOVIE_STATUS_DC);
        MovieEntity oldMovie = movie(30, dcStatus, daysFromToday(5), daysFromToday(-10));
        MovieEntity newMovie = movie(31, scStatus, daysFromToday(5), daysFromToday(-10));
        CinemaTheaterEntity cinemaTheater = CinemaTheaterEntity.builder().cinemaTheaterId(4).build();
        MovieVariationEntity movieVariation = MovieVariationEntity.builder().id(5).build();
        ShowTimeEntity existingShowTime = savedShowTime(oldMovie, cinemaTheater, tomorrow, LocalTime.of(9, 0), LocalTime.of(11, 0), ShowTimeStatus.INVALID);
        existingShowTime.setId(15L);

        ShowTimeRequest request = new ShowTimeRequest();
        request.setMovieId(31);
        request.setCinemaTheaterId(4);
        request.setMovieVariationId(5);
        request.setShowDate(tomorrow);
        request.setStartTime(LocalTime.of(13, 0));
        request.setOriginPrice(120_000D);
        request.setStatus(ShowTimeStatus.VALID);
        request.setSpecial(true);

        when(showTimeRepository.findById(15L)).thenReturn(Optional.of(existingShowTime));
        when(cinemaTheaterRepository.findByStatusAndCinemaTheaterId(any(), eq(4))).thenReturn(Optional.of(cinemaTheater));
        when(movieRepository.findById(31)).thenReturn(Optional.of(newMovie));
        when(movieRepository.findById(30)).thenReturn(Optional.of(oldMovie));
        when(movieVariationRepository.findById(5)).thenReturn(Optional.of(movieVariation));
        when(showTimeRepository.findAllByCinemaTheaterAndShowDateAndStatusNot(eq(cinemaTheater), eq(tomorrow), eq(ShowTimeStatus.DELETED), any(Sort.class)))
                .thenReturn(List.of(existingShowTime));
        when(showTimeRepository.save(existingShowTime)).thenReturn(existingShowTime);
        when(showTimeRepository.findAllByMovieAndStatusNot(newMovie, ShowTimeStatus.DELETED))
                .thenReturn(List.of(savedShowTime(newMovie, cinemaTheater, tomorrow, LocalTime.of(13, 0), LocalTime.of(15, 0), ShowTimeStatus.VALID)));
        when(showTimeRepository.findAllByMovieAndStatusNot(oldMovie, ShowTimeStatus.DELETED))
                .thenReturn(Collections.emptyList());
        when(movieStatusService.findById(MovieStatus.MOVIE_STATUS_DC)).thenReturn(dcStatus);
        when(movieStatusService.findById(MovieStatus.MOVIE_STATUS_SC)).thenReturn(scStatus);
        when(movieService.findById(31)).thenReturn(MovieResponse.builder().movieId(31).title("Movie 31").build());

        showTimeService.update(15L, request);

        assertEquals(31, existingShowTime.getMovie().getMovieId());
        assertEquals(MovieStatus.MOVIE_STATUS_DC, newMovie.getStatus().getStatusId());
        assertEquals(MovieStatus.MOVIE_STATUS_SC, oldMovie.getStatus().getStatusId());
    }

    @Test
    void delete_shouldHandleSqlDateWithoutUnsupportedOperationException() {
        MovieStatusEntity dcStatus = movieStatus(MovieStatus.MOVIE_STATUS_DC);
        MovieStatusEntity scStatus = movieStatus(MovieStatus.MOVIE_STATUS_SC);
        MovieEntity movie = movie(
                40,
                dcStatus,
                java.sql.Date.valueOf(LocalDate.now().plusDays(3)),
                java.sql.Date.valueOf(LocalDate.now().minusDays(10))
        );
        ShowTimeEntity showTime = ShowTimeEntity.builder()
                .id(18L)
                .movie(movie)
                .status(ShowTimeStatus.VALID)
                .build();

        when(showTimeRepository.findByIdAndStatusNot(18L, ShowTimeStatus.DELETED)).thenReturn(Optional.of(showTime));
        when(showTimeRepository.save(showTime)).thenReturn(showTime);
        when(movieRepository.findById(40)).thenReturn(Optional.of(movie));
        when(showTimeRepository.findAllByMovieAndStatusNot(movie, ShowTimeStatus.DELETED)).thenReturn(Collections.emptyList());
        when(movieStatusService.findById(MovieStatus.MOVIE_STATUS_SC)).thenReturn(scStatus);

        showTimeService.delete(18L);

        assertEquals(MovieStatus.MOVIE_STATUS_SC, movie.getStatus().getStatusId());
    }

    private MovieEntity movie(Integer id, MovieStatusEntity status, Date endDate, Date releaseDate) {
        return MovieEntity.builder()
                .movieId(id)
                .status(status)
                .endDate(endDate)
                .releaseDate(releaseDate)
                .duration(120)
                .build();
    }

    private MovieStatusEntity movieStatus(String statusId) {
        return MovieStatusEntity.builder()
                .statusId(statusId)
                .name(statusId)
                .active(true)
                .build();
    }

    private ShowTimeEntity savedShowTime(
            MovieEntity movie,
            CinemaTheaterEntity cinemaTheater,
            Date showDate,
            LocalTime startTime,
            LocalTime endTime,
            ShowTimeStatus status
    ) {
        return ShowTimeEntity.builder()
                .movie(movie)
                .cinemaTheater(cinemaTheater)
                .showDate(showDate)
                .startTime(startTime)
                .endTime(endTime)
                .status(status)
                .special(false)
                .originPrice(100_000D)
                .build();
    }

    private Date daysFromToday(int offset) {
        return Date.from(LocalDate.now()
                .plusDays(offset)
                .atStartOfDay(ZoneId.systemDefault())
                .toInstant());
    }
}
