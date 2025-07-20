package com.codebloom.cineman.controller;

import com.codebloom.cineman.TestSecurityConfig;
import com.codebloom.cineman.controller.admin.FeedbackTopicController;
import com.codebloom.cineman.controller.request.FeedbackTopicRequest;
import com.codebloom.cineman.controller.response.FeedbackTopicResponse;
import com.codebloom.cineman.repository.PermissionRepository;
import com.codebloom.cineman.service.FeedbackTopicService;
import com.codebloom.cineman.service.JwtService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@TestPropertySource(properties = "api.path=/api/v1")
@WebMvcTest(controllers = FeedbackTopicController.class)
@Import(TestSecurityConfig.class)
class FeedbackTopicControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private FeedbackTopicService feedbackTopicService;

    @MockitoBean
    private JwtService jwtService;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PermissionRepository permissionRepository;

    private FeedbackTopicResponse topic1;
    private FeedbackTopicResponse topic2;

    private FeedbackTopicRequest topicRequest;

    private static final String BASE_PATH = "/api/v1/admin/feedback-topics";

    @BeforeEach
    void setup() {
        topic1 = new FeedbackTopicResponse();
        topic1.setTopicId(1);
        topic1.setTopicName("topic1");
        topic1.setDescription("description1");

        topic2 = new FeedbackTopicResponse();
        topic2.setTopicId(2);
        topic2.setTopicName("topic2");
        topic2.setDescription("description2");

        topicRequest = new FeedbackTopicRequest();
        topicRequest.setTopicName("New Topic");
        topicRequest.setDescription("New Description");
    }

    @Test
    @DisplayName("GET /all - Lấy tất cả chủ đề")
    void testGetAllTopics() throws Exception {
        when(feedbackTopicService.findAll()).thenReturn(List.of(topic1, topic2));

        mockMvc.perform(get(BASE_PATH + "/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Lấy danh sách chủ đề phản hồi thành công."))
                .andExpect(jsonPath("$.data.length()").value(2));
    }

    @Test
    @DisplayName("GET /{id} - Lấy chủ đề theo ID")
    void testGetTopicById() throws Exception {
        when(feedbackTopicService.findById(1)).thenReturn(topic1);

        mockMvc.perform(get(BASE_PATH + "/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Lấy thông tin chủ đề phản hồi thành công."))
                .andExpect(jsonPath("$.data.topicId").value(1));
    }

    @Test
    @DisplayName("POST /add - Tạo chủ đề")
    void testCreateTopic() throws Exception {
        FeedbackTopicResponse saved = new FeedbackTopicResponse(3, "New Topic", "New Description");

        when(feedbackTopicService.save(any(FeedbackTopicRequest.class))).thenReturn(saved);

        mockMvc.perform(post(BASE_PATH + "/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(topicRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("Tạo chủ đề phản hồi thành công."))
                .andExpect(jsonPath("$.data.topicId").value(3));
    }

    @Test
    @DisplayName("PUT /{id}/update - Cập nhật chủ đề")
    void testUpdateTopic() throws Exception {
        FeedbackTopicResponse updated = new FeedbackTopicResponse(1, "Updated", "Updated Description");

        when(feedbackTopicService.update(eq(1), any(FeedbackTopicRequest.class))).thenReturn(updated);

        mockMvc.perform(put(BASE_PATH + "/1/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(topicRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Cập nhật chủ đề phản hồi thành công."))
                .andExpect(jsonPath("$.data.topicName").value("Updated"));
    }

    @Test
    @DisplayName("DELETE /{id}/delete - Xoá chủ đề")
    void testDeleteTopic() throws Exception {
        mockMvc.perform(delete(BASE_PATH + "/1/delete"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Xóa chủ đề phản hồi thành công."))
                .andExpect(jsonPath("$.data").value(1));
    }
}
