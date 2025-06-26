package com.codebloom.cineman.service;

import com.codebloom.cineman.controller.request.PageRequest;
import com.codebloom.cineman.controller.request.SeatMapRequest;
import com.codebloom.cineman.controller.response.SeatMapResponse;
import com.codebloom.cineman.model.SeatMapEntity;

import java.util.List;
import java.util.Map;

public interface SeatMapService {

    List<SeatMapEntity> findAll();
    SeatMapResponse findAll(PageRequest pageRequest, Map<String, Object>... params);
    SeatMapEntity findById(Integer id);
    SeatMapEntity save(SeatMapRequest seatMapRequest);
    SeatMapEntity update(Integer id, SeatMapRequest seatMapRequest);
    void deleteById(Integer id);

}
