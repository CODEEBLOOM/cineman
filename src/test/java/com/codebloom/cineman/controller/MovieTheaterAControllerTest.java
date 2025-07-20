package com.codebloom.cineman.controller;

import com.codebloom.cineman.TestSecurityConfig;
import com.codebloom.cineman.controller.admin.MovieTheaterAController;
import com.codebloom.cineman.controller.request.MovieTheaterRequest;
import com.codebloom.cineman.controller.request.PageRequest;
import com.codebloom.cineman.controller.response.*;
import com.codebloom.cineman.repository.PermissionRepository;
import com.codebloom.cineman.service.JwtService;
import com.codebloom.cineman.service.MovieTheaterService;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@TestPropertySource(properties = "api.path=/api/v1")
@WebMvcTest(controllers = MovieTheaterAController.class)
@Import(TestSecurityConfig.class)
class MovieTheaterAControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private PermissionRepository permissionRepository;

    @MockBean
    private MovieTheaterService movieTheaterService;

    @Autowired
    private ObjectMapper objectMapper;

    private final String BASE_URL = "/api/v1/admin/movie-theater";

    private MovieTheaterRequest sampleRequest() {
        MovieTheaterRequest request = new MovieTheaterRequest();
        request.setName("CGV Aeon");
        request.setAddress("Tân Phú");
        request.setHotline("19001234");
        request.setIframeCode("<iframe></iframe>");
        request.setProvinceId(1);
        return request;
    }

    private MovieTheaterResponse sampleResponse() {
        return MovieTheaterResponse.builder()
                .movieTheaterId(1)
                .name("CGV Aeon")
                .address("Tân Phú")
                .hotline("19001234")
                .iframeCode("<iframe></iframe>")
                .status(true)
                .numbersOfCinemaTheater(3)
                .province(null)
                .cinemaTheaters(null)
                .build();
    }

    @Test
    @DisplayName("POST /add - Create Movie Theater")
    void testCreateMovieTheater() throws Exception {
        MovieTheaterRequest request = sampleRequest();
        MovieTheaterResponse response = sampleResponse();

        Mockito.when(movieTheaterService.save(any(MovieTheaterRequest.class))).thenReturn(response);

        mockMvc.perform(post(BASE_URL + "/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value(201))
                .andExpect(jsonPath("$.message").value("Add movie theater success"))
                .andExpect(jsonPath("$.data.name").value("CGV Aeon"));
    }

    @Test
    @DisplayName("PUT /{id}/update - Update Movie Theater")
    void testUpdateMovieTheater() throws Exception {
        MovieTheaterRequest request = sampleRequest();
        MovieTheaterResponse response = sampleResponse();

        Mockito.when(movieTheaterService.update(eq(1), any(MovieTheaterRequest.class))).thenReturn(response);

        mockMvc.perform(put(BASE_URL + "/1/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("Update movie theater success"))
                .andExpect(jsonPath("$.data.name").value("CGV Aeon"));
    }

    @Test
    @DisplayName("DELETE /{id}/delete - Delete Movie Theater")
    void testDeleteMovieTheater() throws Exception {
        doNothing().when(movieTheaterService).delete(1);

        mockMvc.perform(delete(BASE_URL + "/1/delete"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("Update movie theater success"))
                .andExpect(jsonPath("$.data").doesNotExist());
    }

    @Test
    @DisplayName("GET /all - Get All Movie Theaters with Pagination")
    void testGetAllMovieTheaters() throws Exception {
        MovieTheaterResponse response = sampleResponse();

        MovieTheaterPage page = MovieTheaterPage.builder()
                .movieTheaters(List.of(response))
                .meta(new MetaResponse(0, 5, 1, 1))
                .build();

        Mockito.when(movieTheaterService.findAllByPage(any(PageRequest.class))).thenReturn(page);

        mockMvc.perform(get(BASE_URL + "/all")
                        .param("page", "0")
                        .param("size", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("Success"))
                .andExpect(jsonPath("$.data.movieTheaters", hasSize(1)))
                .andExpect(jsonPath("$.data.movieTheaters[0].name").value("CGV Aeon"))
                .andExpect(jsonPath("$.data.meta.totalElements").value(1));
    }

    @Test
    @DisplayName("GET /{id} - Get Movie Theater by ID")
    void testGetMovieTheaterById() throws Exception {
        MovieTheaterResponse response = sampleResponse();

        Mockito.when(movieTheaterService.findById(1)).thenReturn(response);

        mockMvc.perform(get(BASE_URL + "/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("Success"))
                .andExpect(jsonPath("$.data.name").value("CGV Aeon"));
    }
}
