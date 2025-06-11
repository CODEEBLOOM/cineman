package com.codebloom.cineman.service.impl;

import com.codebloom.cineman.controller.request.SignInRequest;
import com.codebloom.cineman.controller.response.TokenResponse;
import com.codebloom.cineman.repository.UserRepository;
import com.codebloom.cineman.service.AuthenticationService;
import com.codebloom.cineman.service.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
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

    @Override
    public TokenResponse getAccessToken(SignInRequest request) throws AccessDeniedException {
        log.info("getAccessToken request {}", request);

        // kiểm tra trong user trong DB có hợp lệ phone, password hay ko
        try{
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getPhoneNumber(), request.getPassword()));
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }catch (AuthenticationException e){
            log.info("login Failed, mesage {}", e.getMessage());
            throw new AccessDeniedException(e.getMessage());
        }

        var user = userRepository.findByPhoneNumber(request.getPhoneNumber());


        String accessToken = jwtService.generateAccessToken(user.getUserId(),request.getPhoneNumber(),user.getAuthorities());
        String refreshToken = jwtService.generateRefreshToken(user.getUserId(),request.getPhoneNumber(),user.getAuthorities());
        return TokenResponse
                .builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    @Override
    public TokenResponse getRefreshToken(String request) {
        return null;
//        log.info("Get refresh token");
//
//        if (!StringUtils.hasLength(refreshToken)) {
//            throw new InvalidDataException("Token must be not blank");
//        }
//
//        try {
//            // Verify token
//            String userName = jwtService.extractUsername(refreshToken, REFRESH_TOKEN);
//
//            // check user is active or inactivated
//            var user = userRepository.findByEmail(userName);
//
//            // generate new access token
//            String accessToken = jwtService.generateAccessToken(user.getUserId(), user.getUsername(), user.getAuthorities());
//
//            return TokenResponse.builder().accessToken(accessToken).refreshToken(refreshToken).build();
//        } catch (Exception e) {
//            log.error("Access denied! errorMessage: {}", e.getMessage());
//            throw new ForBiddenException(e.getMessage());
//        }
//    }
    }
}
