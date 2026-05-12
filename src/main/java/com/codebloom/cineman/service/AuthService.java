package com.codebloom.cineman.service;

import com.codebloom.cineman.controller.request.LoginRequest;
import com.codebloom.cineman.controller.response.TokenResponse;
import com.codebloom.cineman.model.UserEntity;

import java.io.IOException;
import java.util.Map;

public interface AuthService {

    TokenResponse getAccessToken(LoginRequest request);

    TokenResponse getRefreshToken(String request);

    /**
     * Phát access/refresh token cho user đã được xác thực ngoài luồng password
     * (ví dụ: social login Google) — không gọi qua AuthenticationManager.
     */
    TokenResponse getAccessTokenForUser(UserEntity user);

    String generateAuthUrl(String loginType);

    Map<String, Object> authenticateAndFetchProfile(String code, String loginType) throws IOException;



}
