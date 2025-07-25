package com.codebloom.cineman.service;

import com.codebloom.cineman.controller.request.SnackRequest;

import com.codebloom.cineman.controller.response.SnackResponse;
import java.util.List;

public interface SnackService {
    SnackResponse create(SnackRequest request);

    SnackResponse update(int id, SnackRequest request);

    void delete(int id);

    SnackResponse findById(int id);

    List<SnackResponse> findAll();

    List<SnackResponse> findAllComboSnacks();


}
