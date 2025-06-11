package com.codebloom.cineman.service.impl;

import com.codebloom.cineman.common.enums.TokenType;
import com.codebloom.cineman.service.JwtService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import io.jsonwebtoken.Jwts;

import javax.sound.midi.InvalidMidiDataException;
import java.nio.file.AccessDeniedException;
import java.security.Key;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
@Slf4j(topic = "JWT-SERVICE")
public class JwtServiceImpl implements JwtService {


    @Value("${jwt.expiryHours}")
    private long expiryHours;

    @Value("${jwt.expiryMinute}")
    private long expiryMinute;

    @Value("${jwt.accessKey}")
    private String accessKey;

    @Value("${jwt.refreshKey}")
    private String refreshKey;

    @Override
    public String generateAccessToken(Long id, String phoneNumber, Collection<? extends GrantedAuthority> authorities) {
        log.info("generateAccessToken {} with authorities {} ", id, authorities);
        Map<String,Object> claims = new HashMap<>();
        claims.put("UserId",id);
        claims.put("role",authorities);
        return generateAccessToken(claims, phoneNumber);
    }

    @Override
    public String generateRefreshToken(Long id, String phoneNumber, Collection<? extends GrantedAuthority> authorities) {
        log.info("generateRefreshToken {} with authorities {} ", id, authorities);
        Map<String,Object> claims = new HashMap<>();
        claims.put("UserId",id);
        claims.put("role",authorities);
        return generateRefreshToken(claims, phoneNumber);
    }

    @Override
    public String extractUsername(String token, TokenType tokenType) throws AccessDeniedException {
        log.info("extractUsername token {} with type {}", token, tokenType);
        return extractClaims(token,tokenType,Claims::getSubject );
    }

    private <T> T extractClaims(String token, TokenType type, Function<Claims, T> claimsExtractor) throws AccessDeniedException {
        final Claims claims = extractAllClaims(token, type);
        return claimsExtractor.apply(claims);
    }

    private Claims extractAllClaims(String token, TokenType type) throws AccessDeniedException {
        try {
            return Jwts.parser().setSigningKey(accessKey).parseClaimsJws(token).getBody();
        }catch (SignatureException | ExpiredJwtException e){
            throw new AccessDeniedException("Access denied, error :" +  e.getMessage());
        }
    }

    private String generateAccessToken(Map<String,Object> claims, String phoneNumber) {
        log.info("generateToken {} with phoneNumber {}", phoneNumber, claims);
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(phoneNumber)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 60 * 1000 * expiryMinute))
                .signWith(SignatureAlgorithm.HS256, getKey(TokenType.ACCESS_TOKEN))
                .compact();
    }

    private String generateRefreshToken(Map<String,Object> claims, String phoneNumber) {
        log.info("generateToken {} with phoneNumber {}", phoneNumber, claims);
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(phoneNumber)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 3600 * 1000 * expiryHours))
                .signWith(SignatureAlgorithm.HS256, getKey(TokenType.REFRESH_TOKEN))
                .compact();
    }

    private Key getKey(TokenType type) {
        switch (type){
            case ACCESS_TOKEN ->{
                return Keys.hmacShaKeyFor(Decoders.BASE64.decode(accessKey));
            }
            case REFRESH_TOKEN ->{
                return Keys.hmacShaKeyFor(Decoders.BASE64.decode(refreshKey));
            }
            default -> {
                throw new IllegalArgumentException("Invalid token type");
            }
        }
     }

}
