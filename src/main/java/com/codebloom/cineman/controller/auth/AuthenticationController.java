package com.codebloom.cineman.controller.auth;


import com.codebloom.cineman.controller.request.LoginRequest;
import com.codebloom.cineman.controller.request.SignInRequest;
import com.codebloom.cineman.controller.response.TokenResponse;
import com.codebloom.cineman.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/auth")
@Slf4j (topic = "AUTHENTICATION-CONTROLLER")
@RequiredArgsConstructor
@Tag(name = "Authentication Controller")
public class AuthenticationController {

    private final AuthService authService;

    @Operation(summary = "Access Token", description = "Get access token and refresh token by email and password")
    @PostMapping("/access-token")
    public TokenResponse getAccessToken(@RequestBody LoginRequest request) {
        log.info("Access Request Token");
        return authService.getAccessToken(request);
    }

    @Operation(summary = "Refresh Token", description = "Get new refresh token by refresh token")
    @PostMapping("/refresh-token")
    public TokenResponse getRefreshToken(@RequestBody String refreshToken) {
        log.info("Refresh Request Token");
        return authService.getRefreshToken(refreshToken);
    }

}
