package com.codebloom.cineman.config;

import com.codebloom.cineman.common.enums.TokenType;
import com.codebloom.cineman.service.JwtService;
import com.codebloom.cineman.service.MyUserDetailsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j(topic = "WEBSOCKET-AUTH-INTERCEPTOR")
public class WebSocketAuthChannelInterceptor implements ChannelInterceptor {

    private final JwtService jwtService;
    private final MyUserDetailsService myUserDetailsService;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        if (accessor == null || accessor.getCommand() != StompCommand.CONNECT || accessor.getUser() != null) {
            return message;
        }

        String authHeader = resolveAuthorizationHeader(accessor);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return message;
        }

        try {
            String token = authHeader.substring(7);
            String username = jwtService.extractUsername(token, TokenType.ACCESS_TOKEN);
            UserDetails userDetails = myUserDetailsService.loadUserByUsername(username);
            if (userDetails.isEnabled() && jwtService.validateToken(token, TokenType.ACCESS_TOKEN, userDetails)) {
                accessor.setUser(new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                ));
            }
        } catch (Exception e) {
            log.warn("Skip websocket authentication: {}", e.getMessage());
        }

        return message;
    }

    private String resolveAuthorizationHeader(StompHeaderAccessor accessor) {
        List<String> authHeaders = accessor.getNativeHeader("Authorization");
        if (authHeaders != null && !authHeaders.isEmpty()) {
            return authHeaders.get(0);
        }

        List<String> lowercaseHeaders = accessor.getNativeHeader("authorization");
        if (lowercaseHeaders != null && !lowercaseHeaders.isEmpty()) {
            return lowercaseHeaders.get(0);
        }

        return null;
    }
}
