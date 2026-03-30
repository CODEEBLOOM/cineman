package com.codebloom.cineman.config;

import com.codebloom.cineman.common.enums.GenderUser;
import com.codebloom.cineman.common.enums.Method;
import com.codebloom.cineman.common.enums.UserStatus;
import com.codebloom.cineman.model.UserEntity;
import com.codebloom.cineman.repository.UserRepository;
import com.codebloom.cineman.service.JwtService;
import com.codebloom.cineman.service.MyUserDetailsService;
import com.codebloom.cineman.service.PermissionService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.LinkedHashSet;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JwtFilterTest {

    @Mock
    private JwtService jwtService;

    @Mock
    private ApplicationContext context;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PermissionService permissionService;

    @Mock
    private MyUserDetailsService myUserDetailsService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @InjectMocks
    private JwtFilter jwtFilter;

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void doFilterInternalShouldRejectInactiveUser() throws Exception {
        StringWriter responseBody = new StringWriter();
        UserEntity inactiveUser = buildUser(7L, "inactive@cineman.test", UserStatus.INACTIVE);

        when(request.getMethod()).thenReturn("GET");
        when(request.getRequestURI()).thenReturn("/api/admin/user/all");
        when(request.getServletPath()).thenReturn("/api/admin/user/all");
        when(request.getHeader("Authorization")).thenReturn("Bearer access-token");
        when(response.getWriter()).thenReturn(new PrintWriter(responseBody));
        when(jwtService.extractUsername("access-token", com.codebloom.cineman.common.enums.TokenType.ACCESS_TOKEN))
                .thenReturn("inactive@cineman.test");
        when(context.getBean(MyUserDetailsService.class)).thenReturn(myUserDetailsService);
        when(myUserDetailsService.loadUserByUsername("inactive@cineman.test"))
                .thenReturn(new com.codebloom.cineman.model.UserPrincipal(inactiveUser));

        jwtFilter.doFilterInternal(request, response, filterChain);

        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        verify(filterChain, never()).doFilter(request, response);
        verifyNoInteractions(permissionService);
        verify(userRepository, never()).findByEmailAndStatus("inactive@cineman.test", UserStatus.ACTIVE);
    }

    @Test
    void doFilterInternalShouldUseActiveUserForPermissionCheck() throws Exception {
        UserEntity activeUser = buildUser(8L, "active@cineman.test", UserStatus.ACTIVE);
        com.codebloom.cineman.model.UserPrincipal userPrincipal = new com.codebloom.cineman.model.UserPrincipal(activeUser);

        when(request.getMethod()).thenReturn("GET");
        when(request.getRequestURI()).thenReturn("/api/admin/user/8");
        when(request.getServletPath()).thenReturn("/api/admin/user/8");
        when(request.getHeader("Authorization")).thenReturn("Bearer access-token");
        when(jwtService.extractUsername("access-token", com.codebloom.cineman.common.enums.TokenType.ACCESS_TOKEN))
                .thenReturn("active@cineman.test");
        when(context.getBean(MyUserDetailsService.class)).thenReturn(myUserDetailsService);
        when(myUserDetailsService.loadUserByUsername("active@cineman.test"))
                .thenReturn(userPrincipal);
        when(jwtService.validateToken(eq("access-token"), eq(com.codebloom.cineman.common.enums.TokenType.ACCESS_TOKEN), any()))
                .thenReturn(true);
        when(userRepository.findByEmailAndStatus("active@cineman.test", UserStatus.ACTIVE)).thenReturn(Optional.of(activeUser));
        when(permissionService.hasPermission(8L, Method.GET, "/api/admin/user/8")).thenReturn(true);

        jwtFilter.doFilterInternal(request, response, filterChain);

        verify(userRepository).findByEmailAndStatus("active@cineman.test", UserStatus.ACTIVE);
        verify(permissionService).hasPermission(8L, Method.GET, "/api/admin/user/8");
        verify(filterChain).doFilter(request, response);
    }

    private UserEntity buildUser(Long userId, String email, UserStatus status) {
        UserEntity user = UserEntity.builder()
                .userId(userId)
                .email(email)
                .password("encoded-password")
                .fullName("Cinema User")
                .phoneNumber("0123456789")
                .address("Ho Chi Minh")
                .gender(GenderUser.MALE)
                .savePoint(0)
                .status(status)
                .build();
        user.setUserRoles(new LinkedHashSet<>());
        return user;
    }
}
