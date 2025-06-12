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
import org.springframework.security.core.userdetails.UserDetails;
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


    // sinh access token
    @Override
    public String generateAccessToken(Long id, String email, Collection<? extends GrantedAuthority> authorities) {
        log.info("generateAccessToken {} with authorities {} ", id, authorities);
        //tạo claims bao gồm userId và role
        Map<String,Object> claims = new HashMap<>();
        claims.put("UserId",id);
        claims.put("role",authorities);
        return generateAccessToken(claims, email);
    }

    @Override
    public String generateRefreshToken(Long id, String email, Collection<? extends GrantedAuthority> authorities) {
        log.info("generateRefreshToken {} with authorities {} ", id, authorities);
        Map<String,Object> claims = new HashMap<>();
        claims.put("UserId",id);
        claims.put("role",authorities);
        return generateRefreshToken(claims, email);
    }

    //trích xuất email tù token
    @Override
    public String extractUsername(String token, TokenType tokenType) throws AccessDeniedException {
        log.info("extractUsername token {} with type {}", token, tokenType);
        return extractClaims(token,tokenType,Claims::getSubject );
    }


    // kiểm tra token có hợp lệ hay ko
    @Override
    public boolean isTokenValid(String token, UserDetails userDetails, TokenType tokenType) {
        try {
            String username = extractUsername(token, tokenType);
            return username.equals(userDetails.getUsername()) && !isTokenExpired(token, tokenType);
        } catch (Exception e) {
            log.warn("Token invalid: {}", e.getMessage());
            return false;
        }
    }

    // gọi extractAllClaims và truy xuất phoneNumber từ
    private <T> T extractClaims(String token, TokenType type, Function<Claims, T> claimsExtractor) throws AccessDeniedException {
        final Claims claims = extractAllClaims(token, type);
        return claimsExtractor.apply(claims);
    }

    // giải mã và xác minh chữ ký token
    private Claims extractAllClaims(String token, TokenType type) throws AccessDeniedException {
        try {
            //giải mã và xác minh chữ ký
            return Jwts.parser().setSigningKey(getKey(type)).parseClaimsJws(token).getBody();
        }catch (SignatureException | ExpiredJwtException e){
            throw new AccessDeniedException("Access denied, error :" +  e.getMessage());
        }
    }

    // tạo access token đã ký với ACCESS_KEY chứa claims và tồn tại trong expiry minute
    private String generateAccessToken(Map<String,Object> claims, String email) {
        log.info("generateToken {} with email {}", email, claims);
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(email) // subject là username -> cinema : email
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 60 * 1000 * expiryMinute))
                .signWith(SignatureAlgorithm.HS256, getKey(TokenType.ACCESS_TOKEN))
                .compact();
    }
    // tạo refresh token đã ký với REFRESH_KEY chứa claims và tồn tại trong expiry hours
    private String generateRefreshToken(Map<String,Object> claims, String email) {
        log.info("generateToken {} with email {}", email, claims);
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(email) // subject là username -> cinema : email
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 3600 * 1000 * expiryHours))
                .signWith(SignatureAlgorithm.HS256, getKey(TokenType.REFRESH_TOKEN))
                .compact();
    }

    // kiểm tra token còn hạn sử dụng hay ko
    private boolean isTokenExpired(String token, TokenType tokenType) throws AccessDeniedException {
        final Date expiration = extractClaims(token, tokenType, Claims::getExpiration);
        return expiration.before(new Date());
    }

    // chọn key cho  đúng loại ACCESS_TOKEN và REFRESH_TOKEN
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
