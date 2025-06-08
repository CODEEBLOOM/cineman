package com.codebloom.cineman.service;

import com.codebloom.cineman.common.enums.MovieStatus;
import com.codebloom.cineman.model.MovieStatusEntity;

public interface MovieStatusService {
    MovieStatusEntity findById(MovieStatus movieStatusId);
}
