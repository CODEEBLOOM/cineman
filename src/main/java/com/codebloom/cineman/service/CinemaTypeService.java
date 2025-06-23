package com.codebloom.cineman.service;

import com.codebloom.cineman.controller.request.CinemaTypeRequest;
import com.codebloom.cineman.model.CinemaTypeEntity;

import java.util.List;

public interface CinemaTypeService {

    CinemaTypeEntity findById(Integer id);

    List<CinemaTypeEntity> findAll();

    CinemaTypeEntity create(CinemaTypeRequest cinemaTypeRequest);

    CinemaTypeEntity update(Integer id, CinemaTypeRequest cinemaTypeRequest);

    void delete(Integer id);

}
