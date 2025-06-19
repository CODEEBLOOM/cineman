package com.codebloom.cineman.service;

import com.codebloom.cineman.common.enums.TokenType;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

public interface JwtService {

    String generateAccessToken(long userId, String username, Collection<? extends GrantedAuthority> authorities);

    String generateRefreshToken(long userId, String username, Collection<? extends GrantedAuthority> authorities);

    String extractUsername(String token, TokenType type);

    boolean validateToken(String token, TokenType type, UserDetails userDetails);

    boolean isTokenExpired(String token, TokenType type);

    String generateTokenToVerify(String phoneNumber, String email);
}
