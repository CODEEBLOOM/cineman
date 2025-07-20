package com.codebloom.cineman.controller;

import com.codebloom.cineman.TestSecurityConfig;
import com.codebloom.cineman.common.enums.SatisfactionLevel;
import com.codebloom.cineman.controller.admin.FeedbackUController;
import com.codebloom.cineman.controller.request.FeedbackRequest;
import com.codebloom.cineman.controller.response.FeedbackResponse;
import com.codebloom.cineman.model.UserPrincipal;
import com.codebloom.cineman.repository.PermissionRepository;
import com.codebloom.cineman.service.FeedbackService;
import com.codebloom.cineman.service.JwtService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.codebloom.cineman.model.UserEntity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;

import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@TestPropertySource(properties = "api.path=/api/v1")
@WebMvcTest(controllers = FeedbackUController.class)
@Import(TestSecurityConfig.class)
class FeedbackUControllerTest {

    private static final String BASE_PATH = "/api/v1/user/feedbacks";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FeedbackService feedbackService;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private PermissionRepository permissionRepository;

    private UserPrincipal userPrincipal;

    @BeforeEach
    void setup() {

        UserEntity user = new UserEntity();
        user.setUserId(99L);
        user.setEmail("user@gmail.com");
        user.setPassword("123456");
        user.setUserRoles(Collections.emptySet());

        userPrincipal = new UserPrincipal(user);

        SecurityContextHolder.getContext().setAuthentication(
                new TestingAuthenticationToken(userPrincipal, null)
        );
    }

    @Test
    @DisplayName("POST /add - Tạo phản hồi thành công")
    @WithMockUser(username = "user@gmail.com", roles = "USER")
    void testCreateFeedback() throws Exception {
        FeedbackRequest request = new FeedbackRequest();
        request.setFeedbackId(1);
        request.setContent("Dịch vụ tốt");
        request.setSatisfactionLevel(SatisfactionLevel.VERY_SATISFIED);
        request.setReasonForReview("Nice!");
        request.setTopicId(1);
        request.setInvoiceId(1L);

        FeedbackResponse mockResponse = new FeedbackResponse();
        mockResponse.setFeedbackId(10);
        mockResponse.setContent("Dịch vụ tốt");

        when(feedbackService.save(any(FeedbackRequest.class), eq(99L)))
                .thenReturn(mockResponse);

        mockMvc.perform(post(BASE_PATH + "/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("Tạo phản hồi thành công."))
                .andExpect(jsonPath("$.data.feedbackId").value(10));
    }


    @Test
    @DisplayName("GET /my-feedbacks - Lấy phản hồi cá nhân thành công")
    @WithMockUser(username = "user@gmail.com", roles = "USER")
    void testGetMyFeedbacks() throws Exception {
        FeedbackResponse feedback1 = new FeedbackResponse();
        feedback1.setFeedbackId(1);
        feedback1.setContent("Tốt");

        FeedbackResponse feedback2 = new FeedbackResponse();
        feedback2.setFeedbackId(2);
        feedback2.setContent("Ổn");

        when(feedbackService.findByUser(99L)).thenReturn(List.of(feedback1, feedback2));

        mockMvc.perform(get(BASE_PATH + "/my-feedbacks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Lấy phản hồi cá nhân thành công."))
                .andExpect(jsonPath("$.data", hasSize(2)))
                .andExpect(jsonPath("$.data[0].feedbackId").value(1))
                .andExpect(jsonPath("$.data[1].content").value("Ổn"));
    }
}
