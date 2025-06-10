package com.codebloom.cineman.service.impl;

import com.codebloom.cineman.common.enums.MovieStatus;
import com.codebloom.cineman.controller.response.MovieStatusResponse;
import com.codebloom.cineman.model.MovieStatusEntity;
import com.codebloom.cineman.repository.MovieStatusRepository;
import com.codebloom.cineman.service.MovieStatusService;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MovieStatusServiceImpl implements MovieStatusService {

    private final MovieStatusRepository movieStatusRepository;

    @Override
    public MovieStatusEntity findById(MovieStatus movieStatusId) {
        return movieStatusRepository.findById(movieStatusId)
                .orElseThrow(() -> new RuntimeException("Status not found with ID: " + movieStatusId));
    }
}

