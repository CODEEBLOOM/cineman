package com.codebloom.cineman.service;

import com.codebloom.cineman.controller.request.SignInRequest;
import com.codebloom.cineman.controller.response.TokenResponse;

import java.nio.file.AccessDeniedException;

public interface AuthenticationService {
    TokenResponse getAccessToken(SignInRequest request) throws AccessDeniedException;
    TokenResponse getRefreshToken(String request);
}
