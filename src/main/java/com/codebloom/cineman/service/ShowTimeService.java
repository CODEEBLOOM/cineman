package com.codebloom.cineman.service;

import com.codebloom.cineman.controller.request.MoviePageQueryRequest;
import com.codebloom.cineman.controller.request.ShowTimeDetailResponseNew;
import com.codebloom.cineman.controller.request.ShowTimeRequest;
import com.codebloom.cineman.controller.request.ShowTimeRequestNew;
import com.codebloom.cineman.controller.response.MovieResponse;
import com.codebloom.cineman.controller.response.ShowTimeDetailResponse;
import com.codebloom.cineman.controller.response.ShowTimeResponse;
import com.codebloom.cineman.model.SeatMapResponse;
import com.codebloom.cineman.model.ShowTimeEntity;

import java.util.Date;
import java.util.List;

public interface ShowTimeService {

    ShowTimeResponse create(ShowTimeRequest request);
    ShowTimeResponse update(Long id, ShowTimeRequest request);
    List<ShowTimeResponse> findAll();
    ShowTimeResponse findById(Long id);
    void delete(Long id);
    List<ShowTimeResponse> findShowTimeByMovieId(Integer movieId);
    List<ShowTimeResponse> findShowTimeByCinemaTheaterId(Integer cinemaTheaterId);
    List<ShowTimeEntity> findAllShowTimeByMovieIdAndMovieTheaterId(Integer movieId, Integer movieTheaterId);
    List<ShowTimeDetailResponse> findAllShowTimeByMovieIdAndMovieTheaterIdAndShowDateEqual(Integer movieId, Integer cinemaTheaterId, Date showDate);
    Long findCountByShowTimeId(Long showTimeId);

    SeatMapResponse findSeatMapByShowTimeIdAndCinemaTheaterId(Long id, Integer cinemaTheaterId);

    List<Date> findAllShowDateByCinemaTheaterIdInFeatured(Integer cinemaTheaterId);
    List<MovieResponse> findAllMovieByCinemaTheaterIdAndShowDate(Integer cinemaTheaterId, Date showDate, MoviePageQueryRequest req);

    /**
     * Find all showtime by filter
     *
     * @param showTime the showtime request
     * @return the list of showtime detail response
     */
    List<ShowTimeDetailResponse> findAllByFilter(ShowTimeRequestNew showTime);

}
