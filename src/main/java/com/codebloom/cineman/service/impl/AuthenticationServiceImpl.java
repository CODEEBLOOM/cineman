package com.codebloom.cineman.service.impl;

import com.codebloom.cineman.common.enums.TokenType;
import com.codebloom.cineman.controller.request.SignInRequest;
import com.codebloom.cineman.controller.response.TokenResponse;
import com.codebloom.cineman.model.UserEntity;
import com.codebloom.cineman.repository.UserRepository;
import com.codebloom.cineman.service.AuthenticationService;
import com.codebloom.cineman.service.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.token.TokenService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.nio.file.AccessDeniedException;

@Service
@RequiredArgsConstructor
@Slf4j(topic = "AUTHENTICATION-SERVICE")
public class AuthenticationServiceImpl implements AuthenticationService {

    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    // xác thực đăng nhập và tạo token
    @Override
    public TokenResponse getAccessToken(SignInRequest request) throws AccessDeniedException {
        log.info("getAccessToken request {}", request);

        // xac thực người dùng có trong DB hay ko
        try{
            // xác thực thông tin người dùng (phoneNumber và password)
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getPhoneNumber(), request.getPassword()));
            // đúng thì lưu vào hệ thống
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }catch (AuthenticationException e){
            // sai báo lỗi
            log.info("login Failed, mesage {}", e.getMessage());
            throw new BadCredentialsException("Số điện thoại hoặc mật khẩu không đúng");        }
        // lấy thông tin người dùng từ DB
        var user = userRepository.findByPhoneNumber(request.getPhoneNumber());
        log.info("User Info {}", user);

        // sinh access token và refresh token
        String accessToken = jwtService.generateAccessToken(user.getUserId(),request.getPhoneNumber(),user.getAuthorities());

        String refreshToken = jwtService.generateRefreshToken(user.getUserId(),request.getPhoneNumber(),user.getAuthorities());
        log.info("ACCESS TOKEN {}", accessToken);
        log.info("REFRESH TOKEN {}", refreshToken);

        return TokenResponse
                .builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();

    }

    @Override
    public TokenResponse getRefreshToken(String refreshToken) {
        log.info("Get refresh token");

        // kiểm tra token có null hoặc trống không
        if (!StringUtils.hasLength(refreshToken)) {
            throw new IllegalArgumentException("Refresh token must not be blank");
        }

        try {
            // giải mã để lấy số điện thoại từ refresh token
            String phoneNumber = jwtService.extractUsername(refreshToken, TokenType.REFRESH_TOKEN);
            log.info("Phone number extracted from refresh token: {}", phoneNumber);

            // kiểm tra người dùng tồn tại trong DB
            UserEntity user = userRepository.findByPhoneNumber(phoneNumber);
            if (user == null) {
                throw new IllegalArgumentException("User not found for phone number: " + phoneNumber);
            }

            // tạo access token mới
            String newAccessToken = jwtService.generateAccessToken(
                    user.getUserId(),
                    user.getPhoneNumber(),
                    user.getAuthorities()
            );

            // trả về access token mới + refresh token cũ
            return TokenResponse.builder()
                    .accessToken(newAccessToken)
                    .refreshToken(refreshToken)
                    .build();

        } catch (Exception e) {
            log.error("Failed to refresh token: {}", e.getMessage());
            throw new RuntimeException("Invalid or expired refresh token");
        }
    }
}
