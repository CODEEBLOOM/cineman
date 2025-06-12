package com.codebloom.cineman.service.impl;

import com.codebloom.cineman.common.enums.TokenType;
import com.codebloom.cineman.controller.request.RefreshTokenCreationRequest;
import com.codebloom.cineman.controller.request.SignInRequest;
import com.codebloom.cineman.controller.response.TokenResponse;
import com.codebloom.cineman.model.RefreshTokenEntity;
import com.codebloom.cineman.model.UserEntity;
import com.codebloom.cineman.repository.TokenRepositoty;
import com.codebloom.cineman.repository.UserRepository;
import com.codebloom.cineman.service.AuthenticationService;
import com.codebloom.cineman.service.JwtService;
import com.codebloom.cineman.service.TokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Service
@RequiredArgsConstructor
@Slf4j(topic = "AUTHENTICATION-SERVICE")
public class AuthenticationServiceImpl implements AuthenticationService {

    @Value("${jwt.expiryHours}")
    private long expiryHours;

    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final TokenService tokenService;
    private final TokenRepositoty tokenRepositoty;

    // xác thực đăng nhập và tạo token
    @Override
    public TokenResponse getAccessToken(SignInRequest request) throws AccessDeniedException {
        log.info("getAccessToken request {}", request);

        // xac thực người dùng có trong DB hay ko
        try{
            // xác thực thông tin người dùng (Email và password)
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
            // đúng thì lưu vào hệ thống
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }catch (AuthenticationException e){
            // sai báo lỗi
            log.info("login Failed, mesage {}", e.getMessage());
            throw new BadCredentialsException("Email or password incorrect");        }
        // lấy thông tin người dùng từ DB
        UserEntity user = userRepository.findByEmail(request.getEmail());
        log.info("User Info {}", user);

        // sinh access token và refresh token
        String accessToken = jwtService.generateAccessToken(user.getUserId(),request.getEmail(),user.getAuthorities());

        String refreshToken = jwtService.generateRefreshToken(user.getUserId(),request.getEmail(),user.getAuthorities());
        log.info("ACCESS TOKEN {}", accessToken);
        log.info("REFRESH TOKEN {}", refreshToken);

        //lưu refresh token vào DB
        RefreshTokenEntity token = new RefreshTokenEntity();
        token.setToken(refreshToken);
        token.setUser(user);
        token.setCreatedByIp("");
        token.setDeviceInfo("");
        // chuyển kiểu localDate -> Date
        Date expiryDate = Date.from(LocalDateTime.now().plusHours(expiryHours)
                .atZone(ZoneId.systemDefault()).toInstant());
        token.setExpiryDate(expiryDate);
        token.setCreatedAt(new Date());
        token.setRevoked(false);
        tokenService.saveOrUpdate(token);

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
            // giải mã để lấy số email từ refresh token
            String email = jwtService.extractUsername(refreshToken, TokenType.REFRESH_TOKEN);
            log.info("email extracted from refresh token: {}", email);

            // kiểm tra người dùng tồn tại trong DB
            UserEntity user = userRepository.findByEmail(email);
            if (user == null) {
                throw new IllegalArgumentException("User not found for email: " + email);
            }

            // Tìm token trong DB (để kiểm tra revoked và expiry)
            RefreshTokenEntity tokenEntity = tokenRepositoty.findByToken(refreshToken)
                    .orElseThrow(() -> new IllegalArgumentException("Refresh token not found in DB"));

            // Kiểm tra token đã bị thu hồi chưa
            if (tokenEntity.isRevoked()) {
                throw new RuntimeException("Refresh token has been revoked");
            }

            // Kiểm tra token đã hết hạn chưa
            if (tokenEntity.getExpiryDate().before(new Date())) {
                throw new RuntimeException("Refresh token has expired");
            }


            // tạo access token mới
            String newAccessToken = jwtService.generateAccessToken(
                    user.getUserId(),
                    user.getEmail(),
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
