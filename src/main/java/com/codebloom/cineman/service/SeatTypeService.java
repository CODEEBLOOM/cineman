package com.codebloom.cineman.service;


import com.codebloom.cineman.common.enums.SeatType;
import com.codebloom.cineman.controller.request.SeatTypeRequest;
import com.codebloom.cineman.model.SeatTypeEntity;

import java.util.List;

public interface SeatTypeService {

    SeatTypeEntity create(SeatTypeRequest seatTypeRequest);
    SeatTypeEntity update(String id, SeatTypeRequest seatTypeRequest);
    List<SeatTypeEntity> findAll();
    SeatTypeEntity findById(String id);
    void delete(String id);

}
