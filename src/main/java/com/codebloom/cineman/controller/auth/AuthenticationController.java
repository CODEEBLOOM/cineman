package com.codebloom.cineman.controller.auth;


import com.codebloom.cineman.controller.request.ChangePasswordRequest;
import com.codebloom.cineman.controller.request.LoginRequest;
import com.codebloom.cineman.controller.request.UserRegisterRequest;
import com.codebloom.cineman.controller.response.ApiResponse;
import com.codebloom.cineman.controller.response.TokenResponse;
import com.codebloom.cineman.controller.response.UserResponse;
import com.codebloom.cineman.service.AuthService;
import com.codebloom.cineman.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;

import static org.springframework.http.HttpStatus.*;

@RestController
@RequestMapping("${api.path}/auth")
@Slf4j (topic = "AUTHENTICATION-CONTROLLER")
@RequiredArgsConstructor
@Tag(name = "Authentication Controller")
public class AuthenticationController {

    private final AuthService authService;
    private final UserService userService;

    @Operation(summary = "Access Token", description = "Get access token and refresh token by email and password")
    @PostMapping("/login")
    public TokenResponse getAccessToken(@RequestBody LoginRequest loginRequest) {
        log.info("Access Request Token");
        TokenResponse tokenResponse = authService.getAccessToken(loginRequest);
        userService.updateRefreshToken(tokenResponse.getRefreshToken());
        return tokenResponse;
    }

    @Operation(summary = "Refresh Token", description = "Get new refresh token by refresh token")
    @PostMapping("/refresh-token")
    public TokenResponse getRefreshToken(@RequestBody String refreshToken) {
        log.info("Refresh Request Token");
        return authService.getRefreshToken(refreshToken);
    }


    @Operation(summary = "Register User", description = "Register account with role user")
    @PostMapping("/register")
    public ResponseEntity<ApiResponse> registerUser(@RequestBody UserRegisterRequest req) {
        log.info("Register account with role user");
        return ResponseEntity.status(CREATED).body(
                ApiResponse.builder()
                        .data(userService.register(req))
                        .message("User registered successfully")
                        .status(CREATED.value())
                        .build()
        );
    }

    @Operation(summary = "Get information of user", description = "API get information of user by accessToken ")
    @PostMapping("/user")
    public ResponseEntity<ApiResponse> getInfoUser(HttpServletRequest request) {
        final String authHeader = request.getHeader("Authorization");
        if(authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            UserResponse userRes = userService.getInfoUserByAccessToken(token);
            return ResponseEntity.status(HttpStatus.OK).body(
                    ApiResponse.builder()
                            .status(OK.value())
                            .message("Get information of user successfully")
                            .data(userRes)
                            .build()
            );
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                ApiResponse.builder()
                        .status(UNAUTHORIZED.value())
                        .message("Access token required")
                        .data(null)
                        .build()
        );
    }


    @Operation(summary = "Change Password", description = "API dùng để thay đổi mật khẩu của User")
    @PutMapping("/change-pwd")
    public ResponseEntity<ApiResponse> changePassword(@RequestBody ChangePasswordRequest request) {
        userService.changePassword(request);
        return ResponseEntity.status(OK).body(
                ApiResponse.builder()
                        .status(OK.value())
                        .message("Change Password Success")
                        .data(null)
                        .build()
        );
    }

}
