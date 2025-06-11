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

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        log.info("{} {}", request.getMethod(), request.getRequestURI());

        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
             authHeader = authHeader.substring(7);
             log.info("bearer authHeader: {}" ,authHeader.substring(0,20));
        }

        String phoneNumber = "";
        try {
            phoneNumber = jwtService.extractUsername(authHeader, TokenType.ACCESS_TOKEN);
            log.info("phone number: {}", phoneNumber);
        }catch (Exception e){
            throw new RuntimeException();
        }

        UserDetails userDetails = userServiceDetail.userDetailsService().loadUserByUsername(phoneNumber);

        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        securityContext.setAuthentication(authentication);
        SecurityContextHolder.setContext(securityContext);

        filterChain.doFilter(request, response);
    }
}