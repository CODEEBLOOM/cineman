package com.codebloom.cineman.controller;

import com.codebloom.cineman.TestSecurityConfig;
import com.codebloom.cineman.common.enums.SatisfactionLevel;
import com.codebloom.cineman.controller.admin.FeedbackAController;
import com.codebloom.cineman.controller.response.FeedbackResponse;
import com.codebloom.cineman.repository.PermissionRepository;
import com.codebloom.cineman.service.FeedbackService;
import com.codebloom.cineman.service.JwtService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Date;
import java.util.List;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

@TestPropertySource(properties = "api.path=/api/v1")
@WebMvcTest(controllers = FeedbackAController.class)
@Import(TestSecurityConfig.class)
class FeedbackAControllerTest {

    private static final String BASE_PATH = "/api/v1/admin/feedbacks";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FeedbackService feedbackService;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private PermissionRepository permissionRepository;

    private FeedbackResponse feedback1;
    private FeedbackResponse feedback2;

    @BeforeEach
    void setUp() {
        feedback1 = new FeedbackResponse();
        feedback1.setFeedbackId(1);
        feedback1.setContent("Chất lượng ổn");
        feedback1.setSatisfactionLevel(SatisfactionLevel.VERY_SATISFIED);
        feedback1.setReasonForReview("Không có gì phàn nàn");
        feedback1.setDateFeedback(new Date());
        feedback1.setTopicName("Rạp Chiếu");
        feedback1.setUserEmail("user1@gmail.com");
        feedback1.setUserId(1);

        feedback2 = new FeedbackResponse();
        feedback2.setFeedbackId(2);
        feedback2.setContent("Chất lượng tệ");
        feedback2.setSatisfactionLevel(SatisfactionLevel.VERY_DISSATISFIED);
        feedback2.setReasonForReview("Rạp nóng, ồn");
        feedback2.setDateFeedback(new Date());
        feedback2.setTopicName("Rạp Chiếu");
        feedback2.setUserEmail("user2@gmail.com");
        feedback2.setUserId(2);
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    @DisplayName("GET /all/Active - Lấy feedback isActive=true")
    void testGetAllActiveFeedbacks() throws Exception {
        when(feedbackService.findAll()).thenReturn(List.of(feedback1, feedback2));

        mockMvc.perform(get(BASE_PATH + "/all/Active"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Lấy tất cả phản hồi (isActive=true) thành công."))
                .andExpect(jsonPath("$.data", hasSize(2)))
                .andExpect(jsonPath("$.data[0].feedbackId").value(1));
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    @DisplayName("GET /all - Lấy tất cả feedback kể cả isActive=false")
    void testGetAllFeedbacksIncludeInactive() throws Exception {
        when(feedbackService.findAllIncludeInactive()).thenReturn(List.of(feedback1, feedback2));

        mockMvc.perform(get(BASE_PATH + "/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Lấy tất cả phản hồi (bao gồm đã xoá mềm) thành công."))
                .andExpect(jsonPath("$.data", hasSize(2)))
                .andExpect(jsonPath("$.data[1].feedbackId").value(2));
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    @DisplayName("GET /{id} - Lấy feedback theo ID")
    void testGetFeedbackById() throws Exception {
        when(feedbackService.findById(1)).thenReturn(feedback1);

        mockMvc.perform(get(BASE_PATH + "/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Lấy phản hồi thành công."))
                .andExpect(jsonPath("$.data.feedbackId").value(1));
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    @DisplayName("GET /by-email - Lấy feedback theo email")
    void testGetFeedbacksByUserEmail() throws Exception {
        when(feedbackService.findByUserEmail("user1@gmail.com")).thenReturn(List.of(feedback1));

        mockMvc.perform(get(BASE_PATH + "/by-email")
                        .param("email", "user1@gmail.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Lấy phản hồi theo email thành công."))
                .andExpect(jsonPath("$.data", hasSize(1)))
                .andExpect(jsonPath("$.data[0].userEmail").value("user1@gmail.com"));
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    @DisplayName("GET /satisfaction - Lấy feedback theo SatisfactionLevel")
    void testGetFeedbacksBySatisfaction() throws Exception {
        when(feedbackService.findBySatisfactionLevel(SatisfactionLevel.VERY_SATISFIED))
                .thenReturn(List.of(feedback1));

        mockMvc.perform(get(BASE_PATH + "/satisfaction")
                        .param("level", "VERY_SATISFIED"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Lọc phản hồi theo mức độ hài lòng thành công."))
                .andExpect(jsonPath("$.data", hasSize(1)))
                .andExpect(jsonPath("$.data[0].satisfactionLevel").value("VERY_SATISFIED"));
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    @DisplayName("DELETE /{id}/delete - Xoá mềm feedback")
    void testDeleteFeedbackAsAdmin() throws Exception {
        doNothing().when(feedbackService).deleteByAdmin(1);

        mockMvc.perform(delete(BASE_PATH + "/1/delete"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Admin đã xoá phản hồi thành công."))
                .andExpect(jsonPath("$.data").value(1));
    }
}
