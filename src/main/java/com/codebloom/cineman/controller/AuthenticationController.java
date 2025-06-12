package com.codebloom.cineman.controller;



import com.codebloom.cineman.controller.request.RefreshTokenCreationRequest;
import com.codebloom.cineman.controller.request.RefreshTokenRequest;
import com.codebloom.cineman.controller.request.SignInRequest;
import com.codebloom.cineman.controller.response.TokenResponse;
import com.codebloom.cineman.service.AuthenticationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.file.AccessDeniedException;


@RestController
@RequestMapping("/auth")
@Tag(name = "Authentication Controller")
@Slf4j(topic = "AUTHENTICATION-CONTROLLER")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;


    // đăng nhập để lấy token (phoneNumber và password)
    @Operation(summary = "Access token", description = "Get access token and refresh token by phoneNumber and password")
    @PostMapping("/access-token")
    public TokenResponse accessToken(@RequestBody SignInRequest request) throws AccessDeniedException {
        log.info("Access token request");
//        return TokenResponse.builder().accessToken("DUMMY-ACCESS-TOKEN").refreshToken("DUMMY-REFRESH-TOKEN").build();
        return authenticationService.getAccessToken(request);
    }

    @Operation(summary = "Refresh token", description = "Get access token by refresh token")
    @PostMapping("/refresh-token")
    public TokenResponse refreshToken(@RequestBody RefreshTokenRequest request) throws AccessDeniedException {
        log.info("Refresh token request");
//        return TokenResponse.builder().accessToken("DUMMY-NEW-ACCESS-TOKEN").refreshToken("DUMMY-REFRESH-TOKEN").build();
                return authenticationService.getRefreshToken(request.getRefreshToken());
    }
}
