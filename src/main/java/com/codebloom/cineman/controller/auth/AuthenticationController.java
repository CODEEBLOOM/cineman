package com.codebloom.cineman.controller.auth;


import com.codebloom.cineman.common.enums.GenderUser;
import com.codebloom.cineman.controller.request.ChangePasswordRequest;
import com.codebloom.cineman.controller.request.LoginRequest;
import com.codebloom.cineman.controller.request.UserCreationRequest;
import com.codebloom.cineman.controller.request.UserRegisterRequest;
import com.codebloom.cineman.controller.response.ApiResponse;
import com.codebloom.cineman.controller.response.TokenResponse;
import com.codebloom.cineman.controller.response.UserResponse;
import com.codebloom.cineman.exception.InvalidDataException;
import com.codebloom.cineman.service.AuthService;
import com.codebloom.cineman.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.Map;
import java.util.Objects;

import static org.springframework.http.HttpStatus.*;

@RestController
@RequestMapping("${api.path}/auth")
@Slf4j (topic = "AUTHENTICATION-CONTROLLER")
@RequiredArgsConstructor
@Tag(name = "Authentication Controller")
public class AuthenticationController {

    private final AuthService authService;
    private final UserService userService;

    @Value("${jwt.expirationDay}")
    private long expirationDay;

    @Operation(summary = "Access Token", description = "Get access token and refresh token by email and password")
    @PostMapping("/login")
    public ResponseEntity<TokenResponse> getAccessToken(@RequestBody LoginRequest loginRequest) {
        log.info("Access Request Token");
        TokenResponse tokenResponse = authService.getAccessToken(loginRequest);
        userService.updateRefreshToken(tokenResponse.getRefreshToken(), false);
        ResponseCookie responseCookie = ResponseCookie.from("refreshToken", tokenResponse.getRefreshToken())
                .httpOnly(true)
                .path("/")
                .maxAge(1000L * 60 * 60 * 24 * expirationDay)
                .build();
        return ResponseEntity.status(OK)
                .header(HttpHeaders.SET_COOKIE, responseCookie.toString())
                .body(tokenResponse);
    }

    @Operation(summary = "Refresh Token", description = "Get new refresh token by refresh token")
    @PostMapping("/refresh-token")
    public ResponseEntity<TokenResponse> getRefreshToken(@CookieValue(name = "refreshToken") String refreshToken) {
        log.info("Refresh Request Token");
        TokenResponse tokenResponse = authService.getRefreshToken(refreshToken);
        ResponseCookie responseCookie = ResponseCookie.from("refreshToken", tokenResponse.getRefreshToken())
                .httpOnly(true)
                .path("/")
                .maxAge(1000L * 60 * 60 * 24 * expirationDay)
                .build();
        return ResponseEntity.status(OK)
                .header(HttpHeaders.SET_COOKIE, responseCookie.toString())
                .body(tokenResponse);
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

    @Operation(summary = "Logout", description = "API dùng để logout tài khoản")
    @PostMapping("/logout")
    public ResponseEntity<Void> changePassword(@RequestHeader("Authorization") String token) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            String email = authentication.getName();
            if(email.isEmpty()){
                throw new InvalidDataException("Access Token Invalid");
            }
            userService.updateRefreshToken(token.substring(7), true);
            ResponseCookie responseCookie = ResponseCookie.from("refreshToken", null)
                    .httpOnly(true)
                    .path("/")
                    .maxAge(0)
                    .build();
            return ResponseEntity.status(OK)
                    .header(HttpHeaders.SET_COOKIE, responseCookie.toString())
                    .build();
        }
        return ResponseEntity.status(UNAUTHORIZED).body(null);
    }

    @Operation(summary = "Confirm User", description = "API dùng để confirm email")
    @GetMapping("/confirm-email")
    public void confirmEmail(@RequestParam String secretCode, HttpServletResponse response) throws IOException {
        log.info("Confirm Email {}", secretCode);
        try {
            userService.confirmEmail(secretCode);
            response.sendRedirect("http://localhost:3000");
        }catch(Exception e) {
            log.error("Error occur confirm email, error: {}", e.getMessage());
        }finally {
            response.sendRedirect("https://www.facebook.com");
        }
    }


    @GetMapping("/social-login")
    public ResponseEntity<ApiResponse> socialAuth(
            @RequestParam("login_type") String loginType
    ){
        loginType = loginType.trim().toLowerCase();  // Loại bỏ dấu cách và chuyển thành chữ thường
        String url = authService.generateAuthUrl(loginType);
        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.builder()
                        .status(HttpStatus.OK.value())
                        .message("Success")
                        .data(url)
                        .build()
        );
    }

    private ResponseEntity<TokenResponse> loginSocial(
            @Valid @RequestBody UserCreationRequest userLoginDTO
    )  {
        // Gọi hàm loginSocial từ UserService cho đăng nhập mạng xã hội
        LoginRequest loginRequest = userService.loginSocial(userLoginDTO);
        return this.getAccessToken(loginRequest);
    }

    @GetMapping("/social/callback")
    public ResponseEntity<TokenResponse> callback(
            @RequestParam("code") String code,
            @RequestParam("login_type") String loginType
    ) throws Exception {
        // Call the AuthService to get user info
        Map<String, Object> userInfo = authService.authenticateAndFetchProfile(code, loginType);

        if (userInfo == null) {
            return ResponseEntity.badRequest().body(null);
        }

        // Extract user information from userInfo map
        String accountId = "";
        String name = "";
        String picture = "";
        String email = "";

        if (loginType.trim().equals("google")) {
            accountId = (String) Objects.requireNonNullElse(userInfo.get("sub"), "");
            name = (String) Objects.requireNonNullElse(userInfo.get("name"), "");
            picture = (String) Objects.requireNonNullElse(userInfo.get("picture"), "");
            email = (String) Objects.requireNonNullElse(userInfo.get("email"), "");
        }
//        else if (loginType.trim().equals("facebook")) {
//            accountId = (String) Objects.requireNonNullElse(userInfo.get("id"), "");
//            name = (String) Objects.requireNonNullElse(userInfo.get("name"), "");
//            email = (String) Objects.requireNonNullElse(userInfo.get("email"), "");
//            // Lấy URL ảnh từ cấu trúc dữ liệu của Facebook
//            Object pictureObj = userInfo.get("picture");
//            if (pictureObj instanceof Map) {
//                Map<?, ?> pictureData = (Map<?, ?>) pictureObj;
//                Object dataObj = pictureData.get("data");
//                if (dataObj instanceof Map) {
//                    Map<?, ?> dataMap = (Map<?, ?>) dataObj;
//                    Object urlObj = dataMap.get("url");
//                    if (urlObj instanceof String) {
//                        picture = (String) urlObj;
//                    }
//                }
//            }
//        }

        // Tạo đối tượng UserLoginDTO
        UserCreationRequest loginRequest = UserCreationRequest.builder()
                .email(email)
                .password(accountId)
                .fullName(name)
                .phoneNumber("")
                .address("")
                .dateOfBirth(Date.from(LocalDate.now().minusYears(13).atStartOfDay(ZoneId.systemDefault()).toInstant()))
                .avatar(picture)
                .gender(GenderUser.MALE)
                .build();

        if (loginType.trim().equals("google")) {
            loginRequest.setGoogleId(accountId);
        } else if (loginType.trim().equals("facebook")) {
            loginRequest.setFacebookId(accountId);
        }

        return this.loginSocial(loginRequest);
    }

}
