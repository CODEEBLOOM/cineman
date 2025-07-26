package com.codebloom.cineman.controller;


import com.codebloom.cineman.controller.admin.ParticipantAController;
import com.codebloom.cineman.controller.request.ParticipantRequest;
import com.codebloom.cineman.model.ParticipantEntity;
import com.codebloom.cineman.service.ParticipantService;
import com.codebloom.cineman.service.JwtService;
import com.codebloom.cineman.repository.PermissionRepository;
import com.codebloom.cineman.common.enums.GenderUser;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;
import java.util.List;


import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@TestPropertySource(properties = "api.path=/api/v1")
@WebMvcTest(ParticipantAController.class)
@Import(TestSecurityConfig.class)
class ParticipantAControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ParticipantService participantService;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private PermissionRepository permissionRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private final String BASE_PATH = "/api/v1/admin/participant";

    private ParticipantRequest request;

    @BeforeEach
    void setup() {
        request = ParticipantRequest.builder()
                .birthName("Christopher Nolan")
                .nickname("Nolan")
                .gender(GenderUser.MALE)
                .nationality("British-American")
                .miniBio("Renowned director known for mind-bending films.")
                .avatar("https://example.com/avatar.jpg")
                .build();
    }

    @Test
    @DisplayName("GET /all - Should return all participants")
    void getAllParticipants_ShouldReturnList() throws Exception {
        List<ParticipantEntity> data = List.of(
                ParticipantEntity.builder().participantId(1).birthName("Christopher Nolan").build(),
                ParticipantEntity.builder().participantId(2).birthName("James Cameron").build()
        );

        when(participantService.findAll()).thenReturn(data);

        mockMvc.perform(get(BASE_PATH + "/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("Success"))
                .andExpect(jsonPath("$.data", hasSize(2)))
                .andExpect(jsonPath("$.data[0].birthName").value("Christopher Nolan"));
    }


    @Test
    @DisplayName("GET /{id} - Should return a participant by ID")
    void getParticipantById_ShouldReturnOne() throws Exception {
        ParticipantEntity data = ParticipantEntity.builder().participantId(1).birthName("Christopher Nolan").build();

        when(participantService.findById(1)).thenReturn(data);

        mockMvc.perform(get(BASE_PATH + "/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("Success"))
                .andExpect(jsonPath("$.data.birthName").value("Christopher Nolan"));
    }

    @Test
    @DisplayName("POST /add - Should create a new participant")
    void createParticipant_ShouldReturnCreated() throws Exception {
        ParticipantEntity saved = ParticipantEntity.builder().participantId(3).birthName("Christopher Nolan").build();

        when(participantService.save(any(ParticipantRequest.class))).thenReturn(saved);

        mockMvc.perform(post(BASE_PATH + "/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value(201))
                .andExpect(jsonPath("$.message").value("Success"))
                .andExpect(jsonPath("$.data.birthName").value("Christopher Nolan"));
    }


    @Test
    @DisplayName("PUT /{id}/update - Should update a participant")
    void updateParticipant_ShouldReturnUpdated() throws Exception {
        ParticipantEntity updated = ParticipantEntity.builder().participantId(1).birthName("Updated Nolan").build();

        when(participantService.update(eq(1), any(ParticipantRequest.class))).thenReturn(updated);

        mockMvc.perform(put(BASE_PATH + "/1/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("Success"))
                .andExpect(jsonPath("$.data.birthName").value("Updated Nolan"));
    }


    @Test
    @DisplayName("DELETE /{id}/delete - Should delete a participant")
    void deleteParticipant_ShouldReturnSuccess() throws Exception {
        doNothing().when(participantService).delete(1);

        mockMvc.perform(delete(BASE_PATH + "/1/delete"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("Delete participant successful"))
                .andExpect(jsonPath("$.data").doesNotExist());
    }
}
