package com.codebloom.cineman.service;


import com.codebloom.cineman.controller.request.MovieStatusRequest;
import com.codebloom.cineman.model.MovieStatusEntity;

import java.util.List;

public interface MovieStatusService {

    List<MovieStatusEntity> findAll();
    MovieStatusEntity findById(String id);
    MovieStatusEntity save(MovieStatusRequest request);
    MovieStatusEntity update(MovieStatusRequest request);
    void deleteById(String id);

}
