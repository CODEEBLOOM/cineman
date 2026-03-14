package com.codebloom.cineman.service;

import com.codebloom.cineman.common.enums.CinemaTheaterStatus;
import com.codebloom.cineman.controller.request.CinemaTheaterRequest;
import com.codebloom.cineman.controller.request.CinemaTheaterResponse;
import com.codebloom.cineman.controller.request.PageRequest;
import com.codebloom.cineman.model.CinemaTheaterEntity;
import com.codebloom.cineman.model.SeatMapResponse;

import java.util.List;

public interface CinemaTheaterService {

    CinemaTheaterEntity create(CinemaTheaterRequest request);
    CinemaTheaterEntity update(Integer id, CinemaTheaterRequest request);
    CinemaTheaterResponse findAll(PageRequest pageRequest, CinemaTheaterStatus...status);
    CinemaTheaterEntity findById(Integer id);
    void delete(Integer id);
    SeatMapResponse findSeatMapByCinemaTheaterId(Integer cinemaTheaterId);
    void publishedCinemaTheater(Integer id);

    List<CinemaTheaterEntity> findAllByMovieTheaterId(Integer movieTheaterId);
}
