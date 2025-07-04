package com.codebloom.cineman.config;

import com.codebloom.cineman.common.enums.Method;
import com.codebloom.cineman.common.enums.TokenType;
import com.codebloom.cineman.model.PermissionEntity;
import com.codebloom.cineman.repository.PermissionRepository;
import com.codebloom.cineman.service.JwtService;
import com.codebloom.cineman.service.MyUserDetailsService;
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
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Slf4j(topic = "JWT-FILTER")
public class JwtFilter extends OncePerRequestFilter {


    private final JwtService jwtService;
    private final ApplicationContext context;
    private final PermissionRepository permissionRepository;

    @Value("${api.path}")
    private String apiPath;


    /**
     * Hàm filter đứng trước spring security để thực hiện check access token và authority
     * @param request request
     * @param response response
     * @param filterChain cho phép đi tiếp
     * @throws ServletException ném lỗi
     * @throws IOException ném lỗi
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        log.info("{} {}", request.getMethod(), request.getRequestURI());

        List<PermissionEntity> guestPermissions = permissionRepository.findAllByRoleGuest();
        List<Pair<String, Method>> bypassTokens = guestPermissions.stream()
                .map(p -> Pair.of(p.getUrl(), p.getMethod()))
                .collect(Collectors.toList());

        if(isBypassToken(request,bypassTokens)) {
            filterChain.doFilter(request, response); //enable bypass
            return;
        }

        final String authHeader = request.getHeader("Authorization");
        String token = null;
        String username = null;

        if(authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
            try {
                username = jwtService.extractUsername(token, TokenType.ACCESS_TOKEN);
            } catch (Exception e) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json");
                response.getWriter().write(errorResponse(e.getMessage()));
                return;
            }
        }

        if(username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = context.getBean(MyUserDetailsService.class).loadUserByUsername(username);

            if(jwtService.validateToken(token,TokenType.ACCESS_TOKEN, userDetails)){
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
        filterChain.doFilter(request, response);
    }


    /* Những request sau không cần check token*/
    private boolean isBypassToken(@NonNull HttpServletRequest request, List<Pair<String, Method>> bypassTokens) {
        bypassTokens = Arrays.asList(

                Pair.of(String.format("%s/auth/login",apiPath),Method.POST),
                Pair.of(String.format("%s/auth/register",apiPath),Method.POST),
                Pair.of(String.format("%s/auth/refresh-token",apiPath),Method.POST),
                Pair.of(String.format("%s/auth/confirm-email",apiPath),Method.GET),
                Pair.of(String.format("%s/auth/social-login",apiPath),Method.GET),
                Pair.of(String.format("%s/auth/social/callback",apiPath),Method.GET),

                // Swagger
                Pair.of("/api-docs",Method.GET),
                Pair.of("/api-docs/**",Method.GET),
                Pair.of("/swagger-resources",Method.GET),
                Pair.of("/swagger-resources/**",Method.GET),
                Pair.of("/configuration/ui",Method.GET),
                Pair.of("/configuration/security",Method.GET),
                Pair.of("/swagger-ui/**",Method.GET),
                Pair.of("/swagger-ui.html", Method.GET),
                Pair.of("/swagger-ui/index.html", Method.GET)
        );

        String requestPath = request.getServletPath();
        String requestMethod = request.getMethod();

        for (Pair<String, Method> token : bypassTokens) {
            String path = token.getFirst();
            Method method = token.getSecond();
            // Check if the request path and method match any pair in the bypassTokens list
            if (requestPath.matches(path.replace("**", ".*"))
                    && requestMethod.equalsIgnoreCase(method.name())) {
                return true;
            }
        }
        return false;
    }


    /**
     * Create error response with pretty template
     * @param message nội dung lỗi
     * @return chuỗi gson
     */
    private String errorResponse(String message) {
        try {
            ErrorResponse error = new ErrorResponse();
            error.setTimestamp(new Date());
            error.setError("Unauthorized");
            error.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            error.setMessage(message);

            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            return gson.toJson(error);
        } catch (Exception e) {
            return ""; // Return an empty string if serialization fails
        }
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