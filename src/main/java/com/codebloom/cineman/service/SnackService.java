package com.codebloom.cineman.service;


import com.codebloom.cineman.model.SnackEntity;
import com.codebloom.cineman.controller.request.SnackCreationRequest;
import com.codebloom.cineman.controller.request.SnackUpdateRequest;
import com.codebloom.cineman.controller.response.SnackResponse;

import java.util.List;



public interface SnackService {
    List<SnackEntity> findAll();
    SnackResponse findByName(String name);
    Integer save(SnackCreationRequest snack);
    void update(SnackUpdateRequest snack);
    void delete(Long userId);
}
