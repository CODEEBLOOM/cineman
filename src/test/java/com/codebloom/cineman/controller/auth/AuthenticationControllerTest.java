package com.codebloom.cineman.controller.auth;

import com.codebloom.cineman.service.AuthService;
import com.codebloom.cineman.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
class AuthenticationControllerTest {

    @Mock
    private AuthService authService;

    @Mock
    private UserService userService;

    @InjectMocks
    private AuthenticationController authenticationController;

    @Test
    void logoutShouldRejectMissingBearerToken() {
        ResponseEntity<Void> response = authenticationController.logout(null);

        assertEquals(401, response.getStatusCode().value());
        verifyNoInteractions(userService);
    }

    @Test
    void logoutShouldStripBearerPrefixAndClearRefreshCookie() {
        ResponseEntity<Void> response = authenticationController.logout("Bearer access-token-value");

        verify(userService).updateRefreshToken("access-token-value", true);
        assertEquals(200, response.getStatusCode().value());
        assertEquals(true, response.getHeaders().containsKey(HttpHeaders.SET_COOKIE));
    }
}
