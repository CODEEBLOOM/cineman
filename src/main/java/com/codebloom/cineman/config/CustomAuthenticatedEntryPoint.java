package com.codebloom.cineman.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Date;

@Component
public class CustomAuthenticatedEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.getWriter().write(errorResponse(authException.getMessage()));
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
