package com.codebloom.cineman.service.impl;

import com.codebloom.cineman.controller.request.LoginRequest;
import com.codebloom.cineman.controller.response.TokenResponse;
import com.codebloom.cineman.exception.DataNotFoundException;
import com.codebloom.cineman.exception.ForBiddenException;
import com.codebloom.cineman.exception.InvalidDataException;
import com.codebloom.cineman.model.UserEntity;
import com.codebloom.cineman.model.UserPrincipal;
import com.codebloom.cineman.repository.UserRepository;
import com.codebloom.cineman.service.AuthService;
import com.codebloom.cineman.service.JwtService;
import com.codebloom.cineman.service.MyUserDetailsService;
import com.codebloom.cineman.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;


import static com.codebloom.cineman.common.enums.TokenType.REFRESH_TOKEN;


@Service
@RequiredArgsConstructor
@Slf4j(topic = "AUTHENTICATION-SERVICE")
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserService userService;
    private final ApplicationContext context;


    /**
     * Hàm get access token và refresh token
     * @param request Login request object
     * @return access token và refresh token
     */
    @Override
    public TokenResponse getAccessToken(LoginRequest request) {
        log.info("get access token");

        try {
            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                    request.getEmail(), request.getPassword()
            );
            Authentication authentication = authenticationManager.authenticate(authToken);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } catch (AuthenticationException e) {
            log.info("Authentication failed: {}", e.getMessage());
            throw new DataNotFoundException("Username or password is incorrect or non-existent");
        }

        UserEntity user =  userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("Email or password is incorrect"));
        UserPrincipal userPrincipal = new UserPrincipal(user);

        String accessToken =  jwtService.generateAccessToken(user.getUserId(), request.getEmail(), userPrincipal.getAuthorities());
        String refreshToken =  jwtService.generateRefreshToken(user.getUserId(), request.getEmail(), userPrincipal.getAuthorities());
        return TokenResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    /**
     * Hàm refresh token: tạo ra accessToken mới và refresh toke mới
     * @param refreshToken : accessToken
     * @return access token và refresh token
     */
    @Override
    public TokenResponse getRefreshToken(String refreshToken) {
        if (!StringUtils.hasLength(refreshToken)) {
            throw new InvalidDataException("Token must be not blank");
        }
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication != null){
            UserDetails userDetails = context.getBean(MyUserDetailsService.class).loadUserByUsername(jwtService.extractUsername(refreshToken, REFRESH_TOKEN));
            try {
                UserEntity user = userRepository.findByRefreshToken(refreshToken);
                if(!jwtService.validateToken(refreshToken, REFRESH_TOKEN, userDetails)){
                    throw new ForBiddenException("Invalid refresh token");
                }
                // generate new access token
                String accessToken = jwtService.generateAccessToken(user.getUserId(), user.getEmail(), userDetails.getAuthorities());
                String newRefreshToken = jwtService.generateRefreshToken(user.getUserId(), user.getEmail(), userDetails.getAuthorities());
                userService.updateRefreshToken(newRefreshToken, false);
                return TokenResponse.builder().accessToken(accessToken).refreshToken(newRefreshToken).build();
            } catch (Exception e) {
                log.error("Access denied! errorMessage: {}", e.getMessage());
                throw new ForBiddenException(e.getMessage());
            }
        }
        return null;
    }
}
