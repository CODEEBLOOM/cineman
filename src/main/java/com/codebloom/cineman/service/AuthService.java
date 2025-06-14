package com.codebloom.cineman.service;

import com.codebloom.cineman.controller.request.LoginRequest;
import com.codebloom.cineman.controller.response.TokenResponse;

public interface AuthService {

    TokenResponse getAccessToken(LoginRequest request);

    TokenResponse getRefreshToken(String request);

}
