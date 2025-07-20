package com.codebloom.cineman.controller;

import com.codebloom.cineman.TestSecurityConfig;
import com.codebloom.cineman.controller.admin.MovieParticipantAController;
import com.codebloom.cineman.controller.request.MovieParticipantRequest;
import com.codebloom.cineman.model.MovieEntity;
import com.codebloom.cineman.model.MovieParticipantEntity;
import com.codebloom.cineman.model.MovieRoleEntity;
import com.codebloom.cineman.model.ParticipantEntity;
import com.codebloom.cineman.repository.PermissionRepository;
import com.codebloom.cineman.service.JwtService;
import com.codebloom.cineman.service.MovieParticipantService;
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

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = MovieParticipantAController.class)
@TestPropertySource(properties = "api.path=/api/v1")
@Import(TestSecurityConfig.class)
public class MovieParticipantAControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private PermissionRepository permissionRepository;

    @MockBean
    private MovieParticipantService movieParticipantService;

    @Autowired
    private ObjectMapper objectMapper;

    private MovieParticipantEntity createSampleEntity() {
        MovieEntity movie = new MovieEntity();
        movie.setMovieId(1);

        ParticipantEntity participant = new ParticipantEntity();
        participant.setParticipantId(2);

        MovieRoleEntity role = new MovieRoleEntity();
        role.setMovieRoleId(3);

        MovieParticipantEntity entity = new MovieParticipantEntity();
        entity.setId(10);
        entity.setMovie(movie);
        entity.setParticipant(participant);
        entity.setMovieRole(role);

        return entity;
    }

    private MovieParticipantRequest createSampleRequest() {
        MovieParticipantRequest request = new MovieParticipantRequest();
        request.setMovieId(1);
        request.setParticipantId(2);
        request.setMovieRoleId(3);
        return request;
    }

    @Test
    @DisplayName("POST /admin/movie-participant/add - Thành công")
    void testAddDirectorForMovie() throws Exception {
        MovieParticipantRequest request = createSampleRequest();
        MovieParticipantEntity responseEntity = createSampleEntity();

        when(movieParticipantService.addParticipantMovie(any(MovieParticipantRequest.class)))
                .thenReturn(responseEntity);

        mockMvc.perform(post("/api/v1/admin/movie-participant/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Create participant success"))
                .andExpect(jsonPath("$.data.id").value(10));
    }

    @Test
    @DisplayName("PUT /admin/movie-participant/{id}/update - Thành công")
    void testUpdateDirectorForMovie() throws Exception {
        MovieParticipantRequest request = createSampleRequest();
        MovieParticipantEntity responseEntity = createSampleEntity();

        when(movieParticipantService.updateParticipantMovie(eq(10), any(MovieParticipantRequest.class)))
                .thenReturn(responseEntity);

        mockMvc.perform(put("/api/v1/admin/movie-participant/10/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Update participant success"))
                .andExpect(jsonPath("$.data.id").value(10));
    }

    @Test
    @DisplayName("DELETE /admin/movie-participant/delete/{movieId}/{directorId} - Thành công")
    void testDeleteDirectorForMovie() throws Exception {
        doNothing().when(movieParticipantService).deleteParticipantMovie(1, 2);

        mockMvc.perform(delete("/api/v1/admin/movie-participant/delete/1/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Delete director success"))
                .andExpect(jsonPath("$.data").doesNotExist());
    }
}
