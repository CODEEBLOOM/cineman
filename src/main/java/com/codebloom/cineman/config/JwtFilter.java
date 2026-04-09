package com.codebloom.cineman.config;

import com.codebloom.cineman.common.enums.Method;
import com.codebloom.cineman.common.enums.TokenType;
import com.codebloom.cineman.common.enums.UserStatus;
import com.codebloom.cineman.exception.DataNotFoundException;
import com.codebloom.cineman.model.UserEntity;
import com.codebloom.cineman.repository.UserRepository;
import com.codebloom.cineman.service.JwtService;
import com.codebloom.cineman.service.MyUserDetailsService;
import com.codebloom.cineman.service.PermissionService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.data.util.Pair;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Service;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j(topic = "JWT-FILTER")
public class JwtFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final ApplicationContext context;
    private final UserRepository userRepository;
    private final PermissionService permissionService;

    @Value("${api.path}")
    private String apiPath;

    @Value("${security.authorization.enabled:false}")
    private boolean authorizationEnabled;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        log.info("{} {}", request.getMethod(), request.getRequestURI());

        if (isBypassToken(request)) {
            filterChain.doFilter(request, response);
            return;
        }

        final String authHeader = request.getHeader("Authorization");
        if (!authorizationEnabled) {
            authenticateIfPresent(authHeader, request);
            filterChain.doFilter(request, response);
            return;
        }

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            writeErrorResponse(
                    response,
                    "Yeu cau dang nhap de truy cap tai nguyen nay",
                    HttpServletResponse.SC_UNAUTHORIZED,
                    "Unauthorized"
            );
            return;
        }

        if (!authenticateAndAuthorize(authHeader, request, response)) {
            return;
        }

        filterChain.doFilter(request, response);
    }

    private boolean authenticateAndAuthorize(String authHeader, HttpServletRequest request, HttpServletResponse response) throws IOException {
        String token = authHeader.substring(7);
        String username;
        try {
            username = jwtService.extractUsername(token, TokenType.ACCESS_TOKEN);
        } catch (Exception e) {
            writeErrorResponse(
                    response,
                    e.getMessage(),
                    HttpServletResponse.SC_UNAUTHORIZED,
                    "Unauthorized"
            );
            return false;
        }

        if (SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = context.getBean(MyUserDetailsService.class).loadUserByUsername(username);

            if (!userDetails.isEnabled() || !jwtService.validateToken(token, TokenType.ACCESS_TOKEN, userDetails)) {
                writeErrorResponse(
                        response,
                        "Tai khoan khong con hoat dong hoac token khong hop le",
                        HttpServletResponse.SC_UNAUTHORIZED,
                        "Unauthorized"
                );
                return false;
            }

            UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authToken);

            UserEntity userEntity = userRepository.findByEmailAndStatus(username, UserStatus.ACTIVE)
                    .orElseThrow(() -> new DataNotFoundException("Active user not found"));

            boolean allowed = permissionService.hasPermission(
                    userEntity.getUserId(),
                    Method.valueOf(request.getMethod()),
                    request.getRequestURI()
            );

            if (!allowed) {
                writeErrorResponse(
                        response,
                        "Khong co quyen truy cap vao tai nguyen nay!",
                        HttpServletResponse.SC_FORBIDDEN,
                        "Forbidden"
                );
                return false;
            }
        }

        return true;
    }

    private void authenticateIfPresent(String authHeader, HttpServletRequest request) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return;
        }

        try {
            String token = authHeader.substring(7);
            String username = jwtService.extractUsername(token, TokenType.ACCESS_TOKEN);
            if (SecurityContextHolder.getContext().getAuthentication() != null) {
                return;
            }

            UserDetails userDetails = context.getBean(MyUserDetailsService.class).loadUserByUsername(username);
            if (!userDetails.isEnabled() || !jwtService.validateToken(token, TokenType.ACCESS_TOKEN, userDetails)) {
                return;
            }

            UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authToken);
        } catch (Exception e) {
            log.warn("Skip JWT enforcement for {} {}: {}", request.getMethod(), request.getRequestURI(), e.getMessage());
        }
    }

    private boolean isBypassToken(@NonNull HttpServletRequest request) {
        List<Pair<String, Method>> bypassTokens = Arrays.asList(
                Pair.of(String.format("%s/auth/login", apiPath), Method.POST),
                Pair.of(String.format("%s/auth/logout", apiPath), Method.POST),
                Pair.of(String.format("%s/auth/register", apiPath), Method.POST),
                Pair.of(String.format("%s/auth/user", apiPath), Method.GET),
                Pair.of(String.format("%s/auth/refresh-token", apiPath), Method.POST),
                Pair.of(String.format("%s/auth/confirm-email", apiPath), Method.GET),
                Pair.of(String.format("%s/auth/social-login", apiPath), Method.GET),
                Pair.of(String.format("%s/auth/social/callback", apiPath), Method.GET),

                Pair.of(String.format("%s/movie/movie-theater/**", apiPath), Method.GET),
                Pair.of(String.format("%s/movie-theater/all", apiPath), Method.GET),
                Pair.of(String.format("%s/show-times/movie/**/movie-theater/**", apiPath), Method.GET),
                Pair.of(String.format("%s/movie/all", apiPath), Method.GET),
                Pair.of(String.format("%s/movie/**", apiPath), Method.GET),
                Pair.of(String.format("%s/admin/province/all", apiPath), Method.GET),
                Pair.of(String.format("%s/admin/movie-theater/province/**/all", apiPath), Method.GET),

                Pair.of(String.format("%s/show-times/cinema-theater/**", apiPath), Method.GET),
                Pair.of(String.format("%s/show-times/cinema-theater/**/show-date/**", apiPath), Method.GET),

                Pair.of(String.format("%s/storages/**", apiPath), Method.GET),

                Pair.of("/api-docs", Method.GET),
                Pair.of("/api-docs/**", Method.GET),
                Pair.of("/swagger-resources", Method.GET),
                Pair.of("/swagger-resources/**", Method.GET),
                Pair.of("/configuration/ui", Method.GET),
                Pair.of("/configuration/security", Method.GET),
                Pair.of("/swagger-ui/**", Method.GET),
                Pair.of("/swagger-ui.html", Method.GET),
                Pair.of("/swagger-ui/index.html", Method.GET)
        );

        String requestPath = request.getServletPath();
        String requestMethod = request.getMethod();

        for (Pair<String, Method> token : bypassTokens) {
            String path = token.getFirst();
            Method method = token.getSecond();
            if (requestPath.matches(path.replace("**", ".*"))
                    && requestMethod.equalsIgnoreCase(method.name())) {
                return true;
            }
        }
        return false;
    }

    private String errorResponse(String message, int status, String error) {
        try {
            ErrorResponse err = new ErrorResponse();
            err.setTimestamp(new Date());
            err.setError(error);
            err.setStatus(status);
            err.setMessage(message);

            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            return gson.toJson(err);
        } catch (Exception e) {
            return "";
        }
    }

    private void writeErrorResponse(HttpServletResponse response, String message, int status, String error) throws IOException {
        response.setStatus(status);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(errorResponse(message, status, error));
    }

    @Setter
    @Getter
    private static class ErrorResponse {
        private Date timestamp;
        private int status;
        private String error;
        private String message;
    }
}
