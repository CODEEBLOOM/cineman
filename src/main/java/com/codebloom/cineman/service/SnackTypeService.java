package com.codebloom.cineman.service;

import com.codebloom.cineman.controller.request.SnackTypeRequest;
import com.codebloom.cineman.controller.response.SnackTypeResponse;

import java.util.List;

public interface SnackTypeService {
    void delete(Integer id);

    List<SnackTypeResponse> findAll();

    SnackTypeResponse findById(Integer id);

    SnackTypeResponse save(SnackTypeRequest snackTypeDto);

    SnackTypeResponse update(Integer id, SnackTypeRequest request);
}
