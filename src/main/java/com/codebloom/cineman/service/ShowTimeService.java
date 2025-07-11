package com.codebloom.cineman.service;

import com.codebloom.cineman.controller.request.ShowTimeRequest;
import com.codebloom.cineman.controller.response.ShowTimeDetailResponse;
import com.codebloom.cineman.controller.response.ShowTimeResponse;
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

}
