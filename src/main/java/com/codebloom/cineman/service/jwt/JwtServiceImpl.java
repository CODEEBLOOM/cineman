package com.codebloom.cineman.service.jwt;

import com.codebloom.cineman.common.enums.TokenType;
import com.codebloom.cineman.exception.InvalidDataException;
import com.codebloom.cineman.service.JwtService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.InvalidKeyException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.*;
import java.util.function.Function;

import static com.codebloom.cineman.common.enums.TokenType.ACCESS_TOKEN;
import static com.codebloom.cineman.common.enums.TokenType.REFRESH_TOKEN;

@Service
@Slf4j(topic = "JWT-SERVICE-UTIL")
public class JwtServiceImpl implements JwtService {

    @Value("${jwt.expirationDay}")
    private long expirationDay;

    @Value("${jwt.expirationMinutes}")
    private long expirationMinutes;

    @Value("${jwt.accessKey}")
    private String accessKey;

    @Value("${jwt.refreshKey}")
    private String refreshKey;


    @Override
    public String generateAccessToken(long userId, String username, Collection<? extends GrantedAuthority> authorities) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("role", authorities);
        return generateToken(claims, username);
    }

    @Override
    public String generateRefreshToken(long userId, String username, Collection<? extends GrantedAuthority> authorities) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("role", authorities);
        return generateRefreshToken(claims, username);
    }

    @Override
    public String extractUsername(String token, TokenType type) {
        return extractClaim(token, type, Claims::getSubject);
    }

//    public String generateSecretKey()  {
//        try {
//            KeyGenerator keyGen = KeyGenerator.getInstance("HmacSHA256");
//            SecretKey secretKey = keyGen.generateKey();
//            return Base64.getEncoder().encodeToString(secretKey.getEncoded());
//        } catch (NoSuchAlgorithmException e) {
//            log.error("{}",e.getMessage());
//            throw new RuntimeException(e);
//        }
//    }


    private String generateToken(Map<String, Object> claims, String email) {
        try {
            return Jwts.builder()
                    .setClaims(claims)
                    .setSubject(email)
                    .setIssuedAt(new Date(System.currentTimeMillis()))
                    .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * expirationMinutes))
                    .signWith(getKey(ACCESS_TOKEN), SignatureAlgorithm.HS256)
                    .compact();
        } catch (InvalidKeyException e) {
            log.error("{}",e.getMessage());
            return null;
        }
    }

    private String generateRefreshToken(Map<String, Object> claims, String username) {
        try {
            return Jwts.builder()
                    .setClaims(claims)
                    .setSubject(username)
                    .setIssuedAt(new Date(System.currentTimeMillis()))
                    .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24 * expirationDay))
                    .signWith(getKey(REFRESH_TOKEN), SignatureAlgorithm.HS256)
                    .compact();
        } catch (InvalidKeyException e) {
            log.error("{}",e.getMessage());
            return null;
        }
    }



    private Key getKey(TokenType type) {
        log.info("----------[ getKey ]----------");
        switch (type) {
            case ACCESS_TOKEN -> {
                return Keys.hmacShaKeyFor(Decoders.BASE64.decode(accessKey));
            }
            case REFRESH_TOKEN -> {
                return Keys.hmacShaKeyFor(Decoders.BASE64.decode(refreshKey));
            }
            default -> {
                try {
                    throw new InvalidDataException("Invalid token type");
                } catch (InvalidDataException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    private <T> T extractClaim(String token, TokenType tokenType, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token, tokenType);
        return claimsResolver.apply(claims);
    }


    private Claims extractAllClaims(String token, TokenType tokenType) {
        try{
            return Jwts.parserBuilder().setSigningKey(getKey(tokenType)).build().parseClaimsJws(token).getBody();
        }catch (SignatureException | ExpiredJwtException e) {
            throw new AccessDeniedException("Access denied !, error: "+ e.getMessage());
        }
    }


    /**
     * Xác minh user
     * @param token là JWT gốc
     * @param userDetails là user đăng nhập
     * @return true nếu user được sử dụng để token đã sinh ra trước đó và còn hạn
     */
    public boolean validateToken(String token,TokenType tokenType, UserDetails userDetails) {
        final String username = extractUsername(token, tokenType);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token, tokenType));
    }


    /**
     * Kiểm tra hạn của token
     * @param token: token gốc
     * @param tokenType: loại token
     * @return true nếu còn hạn
     */
    private boolean isTokenExpired(String token, TokenType tokenType) {
        return extractExpiration(token, tokenType).before(new Date());
    }

    /**
     * Lấy ra expiry của token
     * @param token token gốc
     * @param tokenType loại token
     * @return Date là ngày hết hạn
     */
    private Date extractExpiration(String token, TokenType tokenType) {
        return extractClaim(token,tokenType, Claims::getExpiration);
    }

}
