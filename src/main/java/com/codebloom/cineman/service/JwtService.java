package com.codebloom.cineman.service;

import com.codebloom.cineman.common.enums.TokenType;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.nio.file.AccessDeniedException;
import java.util.Collection;

public interface JwtService {
    // tạo access token
    String generateAccessToken(Long id, String phoneNumber, Collection<? extends GrantedAuthority> authorities);

    // tạo refresh token
    String generateRefreshToken(Long id, String phoneNumber, Collection<? extends GrantedAuthority> authorities);

    //kiểm tra token
    String extractUsername(String token, TokenType tokenType) throws AccessDeniedException;

    //kiểm tra token có hợp lệ hay ko còn thời gian ko.
    public boolean isTokenValid(String token, UserDetails userDetails, TokenType tokenType);



}
