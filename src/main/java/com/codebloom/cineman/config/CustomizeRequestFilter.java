package com.codebloom.cineman.config;

import com.codebloom.cineman.common.enums.TokenType;
import com.codebloom.cineman.service.JwtService;
import com.codebloom.cineman.service.UserServiceDetail;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@Slf4j(topic = "CUSTOMIZE-REQUEST-FILTER")
@RequiredArgsConstructor
public class CustomizeRequestFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserServiceDetail userServiceDetail;

    // kiểm tra jwt của mỗi request
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        log.info("{} {}", request.getMethod(), request.getRequestURI());
// lấy jwt từ header

        final String authHeader = request.getHeader("Authorization");
        final String token;
        final String phoneNumber = "";

        if (authHeader == null) {
            System.out.println("Authorization header is missing");
        } else {
            System.out.println("Authorization header: " + authHeader);
        }


        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);  // Bỏ qua nếu không có header Authorization hoặc sai định dạng
            return;
        }

        // cắt token -> "Bearer <token>"
        token = authHeader.substring(7);

        if (phoneNumber != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userServiceDetail.userDetailsService().loadUserByUsername(phoneNumber);
            if (jwtService.isTokenValid(token, userDetails, TokenType.ACCESS_TOKEN )) {
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

//        try {
//            phoneNumber = jwtService.extractUsername(token, TokenType.ACCESS_TOKEN);
//            log.info("Phone number from token: {}", phoneNumber);
//        } catch (Exception e) {
//            log.error("Failed to extract phone number from token", e);
//            throw new RuntimeException("Invalid or expired token");
//        }
//
//        // lấy thông tin người dùng từ DB
//        UserDetails userDetails = userServiceDetail.userDetailsService().loadUserByUsername(phoneNumber);
//
//        // tạo UsernamePasswordAuthenticationToken lưu vào hệ thổng để hệ thống biết user này đã được xác thực
//        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
//        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
//        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
//        securityContext.setAuthentication(authentication);
//        SecurityContextHolder.setContext(securityContext);

        filterChain.doFilter(request, response);
    }
}