package com.codebloom.cineman.controller;

import com.codebloom.cineman.TestSecurityConfig;
import com.codebloom.cineman.controller.admin.MovieRoleAController;
import com.codebloom.cineman.controller.request.MovieRoleRequest;
import com.codebloom.cineman.model.MovieRoleEntity;
import com.codebloom.cineman.repository.PermissionRepository;
import com.codebloom.cineman.service.JwtService;
import com.codebloom.cineman.service.MovieRoleService;
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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@TestPropertySource(properties = "api.path=/api/v1")
@WebMvcTest(controllers = MovieRoleAController.class)
@Import(TestSecurityConfig.class)
public class MovieRoleAControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private PermissionRepository permissionRepository;

    @MockBean
    private MovieRoleService movieRoleService;

    @Autowired
    private ObjectMapper objectMapper;

    private MovieRoleEntity sampleMovieRole;

    @BeforeEach
    void setup() {
        sampleMovieRole = new MovieRoleEntity();
        sampleMovieRole.setMovieRoleId(1);
        sampleMovieRole.setName("Đạo diễn");
        sampleMovieRole.setDescription("Mô tả đạo diễn");
        sampleMovieRole.setActive(true);
    }

    private MovieRoleRequest createSampleRequest() {
        MovieRoleRequest req = new MovieRoleRequest();
        req.setName("Diễn viên");
        req.setDescription("Người đảm nhận vai diễn chính");
        return req;
    }

    @Test
    @DisplayName("GET /admin/movie-role/{id} - Tìm movie role theo id")
    void testGetMovieRoleById() throws Exception {
        when(movieRoleService.findById(1)).thenReturn(sampleMovieRole);

        mockMvc.perform(get("/api/v1/admin/movie-role/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.movieRoleId").value(1))
                .andExpect(jsonPath("$.data.name").value("Đạo diễn"));
    }

    @Test
    @DisplayName("GET /admin/movie-role/all - Lấy tất cả movie role")
    void testGetAllMovieRoles() throws Exception {
        List<MovieRoleEntity> movieRoles = Arrays.asList(sampleMovieRole);
        when(movieRoleService.findAll()).thenReturn(movieRoles);

        mockMvc.perform(get("/api/v1/admin/movie-role/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].movieRoleId").value(1))
                .andExpect(jsonPath("$.data[0].name").value("Đạo diễn"));
    }

    @Test
    @DisplayName("POST /admin/movie-role/add - Tạo mới movie role thành công")
    void testAddMovieRole() throws Exception {
        MovieRoleRequest request = createSampleRequest();

        MovieRoleEntity savedEntity = MovieRoleEntity.builder()
                .movieRoleId(1)
                .name("Đạo diễn")
                .description("Mô tả đạo diễn")
                .active(true)
                .build();

        when(movieRoleService.create(any(MovieRoleRequest.class))).thenReturn(savedEntity);

        mockMvc.perform(post("/api/v1/admin/movie-role/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("Create movie role successfully"))
                .andExpect(jsonPath("$.data.movieRoleId").value(1))
                .andExpect(jsonPath("$.data.name").value("Đạo diễn"))
                .andExpect(jsonPath("$.data.description").value("Mô tả đạo diễn"))
                .andExpect(jsonPath("$.data.active").value(true));

        verify(movieRoleService).create(any(MovieRoleRequest.class));
    }

    @Test
    @DisplayName("PUT /admin/movie-role/{id}/update - Cập nhật movie role")
    void testUpdateMovieRole() throws Exception {
        MovieRoleRequest request = createSampleRequest();

        when(movieRoleService.update(eq(1), any(MovieRoleRequest.class))).thenReturn(sampleMovieRole);

        mockMvc.perform(put("/api/v1/admin/movie-role/1/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.movieRoleId").value(1))
                .andExpect(jsonPath("$.data.name").value("Đạo diễn"));

        verify(movieRoleService).update(eq(1), any(MovieRoleRequest.class));
    }

    @Test
    @DisplayName("DELETE /admin/movie-role/{id}/delete - Xoá mềm movie role")
    void testDeleteMovieRole() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .delete("/api/v1/admin/movie-role/1/delete"))
                .andExpect(status().isBadRequest()) // expect HTTP 400
                .andExpect(jsonPath("$.message").value("Delete movie role successfully"));

        verify(movieRoleService).delete(1);
    }

}
