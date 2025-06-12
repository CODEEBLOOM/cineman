package com.codebloom.cineman.service;


import com.codebloom.cineman.controller.request.RefreshTokenCreationRequest;
import com.codebloom.cineman.controller.request.RefreshTokenUpdateRequest;
import com.codebloom.cineman.model.RefreshTokenEntity;

import java.util.Optional;

public interface TokenService {
    void saveOrUpdate(RefreshTokenEntity request);
}
