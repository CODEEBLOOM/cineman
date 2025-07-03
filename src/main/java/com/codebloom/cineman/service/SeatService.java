package com.codebloom.cineman.service;

import com.codebloom.cineman.controller.request.SeatRequest;
import com.codebloom.cineman.controller.response.SeatResponse;
import com.codebloom.cineman.model.SeatEntity;

import java.util.List;

public interface SeatService {

    SeatResponse findById(Integer cinemaTheaterId, long id);
    List<SeatResponse> findAllByCinemaTheater(Integer cinemaTheaterId);
    SeatEntity save(SeatRequest seat);
    SeatEntity update(long id, SeatRequest seat);
    void delete(long id);
    List<SeatResponse> addMultiple(List<SeatRequest> seats);
    void deleteMultiple(List<Long> ids);
    SeatResponse changeStatus(long id);

}
