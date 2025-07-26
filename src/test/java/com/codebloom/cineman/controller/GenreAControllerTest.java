package com.codebloom.cineman.controller;


import com.codebloom.cineman.controller.admin.GenreAController;
import com.codebloom.cineman.controller.request.GenresRequest;
import com.codebloom.cineman.model.GenresEntity;
import com.codebloom.cineman.repository.PermissionRepository;
import com.codebloom.cineman.service.GenreService;
import com.codebloom.cineman.service.JwtService;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@TestPropertySource(properties = "api.path=/api/v1")
@WebMvcTest(GenreAController.class)
@Import(TestSecurityConfig.class)
class GenreAControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GenreService genreService;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private PermissionRepository permissionRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private final String BASE_PATH = "/api/v1/admin/genre";

    private GenresRequest request;

    @BeforeEach
    void setup() {
        request = new GenresRequest();
        request.setName("Test Genre");
        request.setDescription("Test Description");
    }

    @Test
    @DisplayName("GET /all - Should return all genres")
    void getAllGenres_ShouldReturnList() throws Exception {
        List<GenresEntity> genres = List.of(
                GenresEntity.builder().genresId(1).name("Action").active(true).build(),
                GenresEntity.builder().genresId(2).name("Comedy").active(true).build()
        );

        when(genreService.findAll()).thenReturn(genres);

        mockMvc.perform(get(BASE_PATH + "/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data", hasSize(2)))
                .andExpect(jsonPath("$.data[0].name").value("Action"));

        verify(genreService).findAll();
    }

    @Test
    @DisplayName("GET /{id} - Should return genre by ID")
    void getGenreById_ShouldReturnGenre() throws Exception {
        GenresEntity genre = GenresEntity.builder()
                .genresId(1)
                .name("Horror")
                .active(true)
                .build();

        when(genreService.findById(1)).thenReturn(genre);

        mockMvc.perform(get(BASE_PATH + "/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data.genresId").value(1))
                .andExpect(jsonPath("$.data.name").value("Horror"));
    }

    @Test
    @DisplayName("POST /add - Should create new genre")
    void createNewGenre_ShouldReturnCreated() throws Exception {
        GenresEntity saved = GenresEntity.builder()
                .genresId(3)
                .name("Thriller")
                .description("Exciting content")
                .active(true)
                .build();

        when(genreService.create(any())).thenReturn(saved);

        mockMvc.perform(post(BASE_PATH + "/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value(201))
                .andExpect(jsonPath("$.data.genresId").value(3))
                .andExpect(jsonPath("$.data.name").value("Thriller"));
    }

    @Test
    @DisplayName("PUT /{id}/update - Should update genre")
    void updateGenre_ShouldReturnUpdated() throws Exception {
        GenresEntity updated = GenresEntity.builder()
                .genresId(5)
                .name("Drama")
                .description("Updated desc")
                .active(true)
                .build();

        when(genreService.update(eq(5), any())).thenReturn(updated);

        mockMvc.perform(put(BASE_PATH + "/5/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data.name").value("Drama"))
                .andExpect(jsonPath("$.data.description").value("Updated desc"));
    }

    @Test
    @DisplayName("DELETE /{id}/delete - Should delete genre (soft delete)")
    void deleteGenre_ShouldReturnSuccess() throws Exception {
        doNothing().when(genreService).delete(7);

        mockMvc.perform(delete(BASE_PATH + "/7/delete"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("Delete genre successfully"));
    }
}
