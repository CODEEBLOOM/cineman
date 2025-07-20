package com.codebloom.cineman.controller;

import com.codebloom.cineman.TestSecurityConfig;
import com.codebloom.cineman.controller.admin.MovieAController;
import com.codebloom.cineman.controller.request.MovieCreationRequest;
import com.codebloom.cineman.controller.request.MoviePageQueryRequest;
import com.codebloom.cineman.controller.request.MovieUpdateRequest;
import com.codebloom.cineman.controller.response.MetaResponse;
import com.codebloom.cineman.controller.response.MoviePageableResponse;
import com.codebloom.cineman.controller.response.MovieResponse;
import com.codebloom.cineman.model.MovieEntity;
import com.codebloom.cineman.model.MovieStatusEntity;
import com.codebloom.cineman.repository.PermissionRepository;
import com.codebloom.cineman.service.JwtService;
import com.codebloom.cineman.service.MovieService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.hasSize;

@TestPropertySource(properties = "api.path=/api/v1")
@WebMvcTest(controllers = MovieAController.class)
@Import(TestSecurityConfig.class)
class MovieAControllerTest {

    private static final String BASE_PATH = "/api/v1/admin/movie";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private PermissionRepository permissionRepository;

    @MockBean
    private MovieService movieService;

    @Autowired
    private ObjectMapper objectMapper;

    private MovieCreationRequest creationRequest;
    private MovieUpdateRequest updateRequest;
    private MovieResponse movieResponse;
    private MovieEntity movieEntity;
    private MovieStatusEntity statusEntity;

    @BeforeEach
    void setUp() {
        creationRequest = new MovieCreationRequest();
        creationRequest.setTitle("Inception");
        creationRequest.setSynopsis("Mind-bending thriller");
        creationRequest.setDetailDescription("A thief who steals corporate secrets...");
        creationRequest.setReleaseDate(new Date());
        creationRequest.setLanguage("English");
        creationRequest.setDuration(148);
        creationRequest.setAge(13);
        creationRequest.setTrailerLink("http://trailer.inception.com");
        creationRequest.setPosterImage("poster.jpg");
        creationRequest.setBannerImage("banner.jpg");

        updateRequest = new MovieUpdateRequest();
        updateRequest.setMovieId(1);
        updateRequest.setTitle("Updated Inception");
        updateRequest.setSynopsis("Updated synopsis");
        updateRequest.setDetailDescription("Updated details");
        updateRequest.setReleaseDate(new Date());
        updateRequest.setLanguage("English");
        updateRequest.setDuration(150);
        updateRequest.setAge(13);
        updateRequest.setTrailerLink("http://trailer.updated.com");
        updateRequest.setPosterImage("poster_updated.jpg");
        updateRequest.setBannerImage("banner_updated.jpg");

        movieResponse = new MovieResponse();
        movieResponse.setMovieId(1);
        movieResponse.setTitle("Inception");
        movieResponse.setStatus("ACTIVE");
        movieResponse.setReleaseDate(new Date());

        statusEntity = new MovieStatusEntity();
        statusEntity.setStatusId("1");
        statusEntity.setName("ACTIVE");

        movieEntity = new MovieEntity();
        movieEntity.setMovieId(1);
        movieEntity.setTitle("Inception");
        movieEntity.setStatus(statusEntity);
        movieEntity.setReleaseDate(new Date());
    }

    @Test
    @DisplayName("POST /add - Tạo phim mới")
    void testCreateMovie() throws Exception {
        when(movieService.save(any(MovieCreationRequest.class))).thenReturn(movieEntity);

        mockMvc.perform(post(BASE_PATH + "/add")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(creationRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("Created movie successfully"))
                .andExpect(jsonPath("$.data.title").value("Inception"));
    }

    @Test
    @DisplayName("PUT /update - Cập nhật phim")
    void testUpdateMovie() throws Exception {

        Calendar calendar = Calendar.getInstance();
        calendar.set(2025, Calendar.AUGUST, 30, 0, 0, 0);
        Date futureDate = calendar.getTime();

        updateRequest.setTitle("Updated Inception");
        updateRequest.setSynopsis("Updated synopsis");
        updateRequest.setDetailDescription("Updated details");
        updateRequest.setReleaseDate(futureDate);
        updateRequest.setLanguage("English");
        updateRequest.setDuration(150);
        updateRequest.setAge(13);
        updateRequest.setTrailerLink("http://trailer.updated.com");
        updateRequest.setPosterImage("poster_updated.jpg");
        updateRequest.setBannerImage("banner_updated.jpg");
        updateRequest.setStatus("ACTIVE");

        MovieResponse updatedResponse = new MovieResponse();
        updatedResponse.setMovieId(1);
        updatedResponse.setTitle("Updated Inception");
        updatedResponse.setStatus("ACTIVE");
        updatedResponse.setReleaseDate(futureDate);

        when(movieService.update(any(MovieUpdateRequest.class))).thenReturn(updatedResponse);

        mockMvc.perform(put(BASE_PATH + "/update")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Updated movie successfully"))
                .andExpect(jsonPath("$.data.title").value("Updated Inception"));
    }


    @Test
    @DisplayName("GET /all - Lấy tất cả phim")
    void testGetAllMovies() throws Exception {
        MetaResponse meta = MetaResponse.builder()
                .currentPage(1)
                .pageSize(10)
                .totalPages(1)
                .totalElements(1)
                .build();

        MoviePageableResponse pageableResponse = MoviePageableResponse.builder()
                .movies(List.of(movieResponse))
                .meta(meta)
                .build();

        when(movieService.findAllByPage(any(MoviePageQueryRequest.class)))
                .thenReturn(pageableResponse);

        mockMvc.perform(get(BASE_PATH + "/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.movies", hasSize(1)))
                .andExpect(jsonPath("$.data.movies[0].title").value("Inception"));
    }

    @Test
    @DisplayName("GET /{id} - Lấy phim theo ID")
    void testGetMovieById() throws Exception {
        when(movieService.findById(1)).thenReturn(movieResponse);

        mockMvc.perform(get(BASE_PATH + "/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.title").value("Inception"))
                .andExpect(jsonPath("$.message").value("Success"));
    }

    @Test
    @DisplayName("DELETE /{id}/delete - Xoá phim")
    void testDeleteMovie() throws Exception {
        doNothing().when(movieService).delete(1);

        mockMvc.perform(delete(BASE_PATH + "/1/delete"))
                .andExpect(status().isNoContent())
                .andExpect(jsonPath("$.message").value("Delete Movie Successfully"));
    }
}
