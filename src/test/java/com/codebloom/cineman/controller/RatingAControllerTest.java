package com.codebloom.cineman.controller;

import com.codebloom.cineman.TestSecurityConfig;
import com.codebloom.cineman.controller.admin.RatingAController;
import com.codebloom.cineman.controller.response.RatingResponse;
import com.codebloom.cineman.repository.PermissionRepository;
import com.codebloom.cineman.service.JwtService;
import com.codebloom.cineman.service.RatingService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@TestPropertySource(properties = "api.path=/api/v1")
@WebMvcTest(controllers = RatingAController.class)
@Import(TestSecurityConfig.class)
class RatingAControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private PermissionRepository permissionRepository;

    @MockBean
    private RatingService ratingService;

    private final String BASE_URL = "/api/v1/admin/ratings";

    private RatingResponse createSampleRatingResponse() {
        return RatingResponse.builder()
                .ticketId(1L)
                .movieId(10)
                .movieTitle("Inception")
                .rating(5)
                .averageRating(4.8)
                .userEmail("user@example.com")
                .build();
    }

    @Test
    @DisplayName("GET /movie/{movieId} - Success")
    void testGetRatingsByMovie_Success() throws Exception {
        RatingResponse rating = createSampleRatingResponse();

        when(ratingService.getRatingsByMovie(10)).thenReturn(List.of(rating));

        mockMvc.perform(get(BASE_URL + "/movie/10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("Lấy đánh giá theo phim thành công."))
                .andExpect(jsonPath("$.data", hasSize(1)))
                .andExpect(jsonPath("$.data[0].movieTitle").value("Inception"))
                .andExpect(jsonPath("$.data[0].rating").value(5));
    }

    @Test
    @DisplayName("GET /movie/{movieId} - Invalid Movie ID (movieId < 1)")
    void testGetRatingsByMovie_InvalidMovieId() throws Exception {
        mockMvc.perform(get(BASE_URL + "/movie/0"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400)) // sửa lại ở đây
                .andExpect(jsonPath("$.message").value("must be greater than or equal to 1"));
    }


    @Test
    @DisplayName("GET /by-email?email=... - Success")
    void testGetRatingsByUserEmail_Success() throws Exception {
        RatingResponse rating = createSampleRatingResponse();

        when(ratingService.getRatingsByEmail("user@example.com")).thenReturn(List.of(rating));

        mockMvc.perform(get(BASE_URL + "/by-email")
                        .param("email", "user@example.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("Lấy đánh giá theo email thành công."))
                .andExpect(jsonPath("$.data", hasSize(1)))
                .andExpect(jsonPath("$.data[0].userEmail").value("user@example.com"));
    }

    @Test
    @DisplayName("GET /by-email?email= - Missing Email Param")
    void testGetRatingsByUserEmail_MissingEmailParam() throws Exception {
        mockMvc.perform(get(BASE_URL + "/by-email"))
                .andExpect(status().isBadRequest());
    }
}
