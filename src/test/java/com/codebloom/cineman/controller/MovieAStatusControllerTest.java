package com.codebloom.cineman.controller;

import com.codebloom.cineman.TestSecurityConfig;
import com.codebloom.cineman.controller.admin.MovieAStatusController;
import com.codebloom.cineman.controller.request.MovieStatusRequest;
import com.codebloom.cineman.model.MovieStatusEntity;
import com.codebloom.cineman.repository.PermissionRepository;
import com.codebloom.cineman.service.JwtService;
import com.codebloom.cineman.service.MovieStatusService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@TestPropertySource(properties = "api.path=/api/v1")
@WebMvcTest(controllers = MovieAStatusController.class)
@Import(TestSecurityConfig.class)
public class MovieAStatusControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private PermissionRepository permissionRepository;

    @MockBean
    private MovieStatusService movieStatusService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("GET /admin/movie-status/all - success")
    void testGetAllMovieStatus() throws Exception {
        MovieStatusEntity status = MovieStatusEntity.builder()
                .statusId("COMING")
                .name("Coming Soon")
                .description("Sắp chiếu")
                .active(true)
                .build();

        when(movieStatusService.findAll()).thenReturn(List.of(status));

        mockMvc.perform(get("/api/v1/admin/movie-status/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].statusId").value("COMING"))
                .andExpect(jsonPath("$.message").value("Success"));
    }

    @Test
    @DisplayName("GET /admin/movie-status/{id} - success")
    void testGetMovieStatusById() throws Exception {
        MovieStatusEntity status = MovieStatusEntity.builder()
                .statusId("DRAFT")
                .name("Draft")
                .description("Chưa công bố")
                .active(true)
                .build();

        when(movieStatusService.findById("DRAFT")).thenReturn(status);

        mockMvc.perform(get("/api/v1/admin/movie-status/DRAFT"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.statusId").value("DRAFT"))
                .andExpect(jsonPath("$.data.name").value("Draft"));
    }

    @Test
    @DisplayName("POST /admin/movie-status/add - success")
    void testAddMovieStatus() throws Exception {
        MovieStatusRequest request = MovieStatusRequest.builder()
                .id("RELEASED")
                .name("Released")
                .description("Đã công chiếu")
                .build();

        MovieStatusEntity savedEntity = MovieStatusEntity.builder()
                .statusId("RELEASED")
                .name("Released")
                .description("Đã công chiếu")
                .active(true)
                .build();

        when(movieStatusService.save(any(MovieStatusRequest.class))).thenReturn(savedEntity);

        mockMvc.perform(post("/api/v1/admin/movie-status/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.statusId").value("RELEASED"))
                .andExpect(jsonPath("$.message").value("Created"));
    }

    @Test
    @DisplayName("PUT /admin/movie-status/update - success")
    void testUpdateMovieStatus() throws Exception {
        MovieStatusRequest request = MovieStatusRequest.builder()
                .id("DRAFT")
                .name("Bản nháp")
                .description("Đang chỉnh sửa")
                .build();

        MovieStatusEntity updatedEntity = MovieStatusEntity.builder()
                .statusId("DRAFT")
                .name("Bản nháp")
                .description("Đang chỉnh sửa")
                .active(true)
                .build();

        when(movieStatusService.update(any(MovieStatusRequest.class))).thenReturn(updatedEntity);

        mockMvc.perform(put("/api/v1/admin/movie-status/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.statusId").value("DRAFT"))
                .andExpect(jsonPath("$.message").value("Updated successfully"));
    }

    @Test
    @DisplayName("DELETE /admin/movie-status/{id}/remove - success")
    void testDeleteMovieStatus() throws Exception {
        doNothing().when(movieStatusService).deleteById("COMING");

        mockMvc.perform(delete("/api/v1/admin/movie-status/COMING/remove"))
                .andExpect(status().isNoContent())
                .andExpect(jsonPath("$.message").value("Delete success with id: COMING"));
    }
}
