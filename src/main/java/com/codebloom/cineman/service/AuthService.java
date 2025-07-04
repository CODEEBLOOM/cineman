package com.codebloom.cineman.service;

import com.codebloom.cineman.controller.request.LoginRequest;
import com.codebloom.cineman.controller.response.TokenResponse;

import java.io.IOException;
import java.util.Map;

public interface AuthService {

    TokenResponse getAccessToken(LoginRequest request);

    TokenResponse getRefreshToken(String request);

    String generateAuthUrl(String loginType);

    Map<String, Object> authenticateAndFetchProfile(String code, String loginType) throws IOException;



}
