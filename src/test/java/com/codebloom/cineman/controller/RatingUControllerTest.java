package com.codebloom.cineman.controller;

import com.codebloom.cineman.TestSecurityConfig;
import com.codebloom.cineman.common.enums.Rating;
import com.codebloom.cineman.common.enums.UserStatus;
import com.codebloom.cineman.controller.admin.RatingUController;
import com.codebloom.cineman.controller.request.RatingRequest;
import com.codebloom.cineman.controller.response.RatingResponse;
import com.codebloom.cineman.model.UserEntity;
import com.codebloom.cineman.model.UserPrincipal;
import com.codebloom.cineman.repository.PermissionRepository;
import com.codebloom.cineman.service.JwtService;
import com.codebloom.cineman.service.RatingService;
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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.HashSet;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@TestPropertySource(properties = "api.path=/api/v1")
@WebMvcTest(controllers = RatingUController.class)
@Import(TestSecurityConfig.class)
class RatingUControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private PermissionRepository permissionRepository;

    @MockBean
    private RatingService ratingService;

    @Autowired
    private ObjectMapper objectMapper;

    private final String basePath = "/api/v1/user/ratings";

    private UserPrincipal createMockUserPrincipal() {
        UserEntity user = new UserEntity();
        user.setUserId(1L);
        user.setEmail("user@gmail.com");
        user.setPassword("password");
        user.setStatus(UserStatus.ACTIVE);
        user.setUserRoles(new HashSet<>());

        return new UserPrincipal(user);
    }

    private final UserPrincipal mockUser = createMockUserPrincipal();

    private void setupAuthentication() {
        Authentication authentication = Mockito.mock(Authentication.class);
        Mockito.when(authentication.getPrincipal()).thenReturn(mockUser);
        SecurityContext context = Mockito.mock(SecurityContext.class);
        Mockito.when(context.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(context);
    }

    @Test
    @DisplayName("POST /add - Thêm đánh giá thành công")
    void testAddRatingSuccess() throws Exception {
        setupAuthentication();

        RatingRequest request = new RatingRequest();
        request.setTicketId(10L);
        request.setRating(Rating.GOOD.ordinal() + 1);

        RatingResponse response = RatingResponse.builder()
                .ticketId(10L)
                .movieTitle("Inception")
                .rating(Rating.GOOD.ordinal() + 1)
                .build();

        Mockito.when(ratingService.saveRating(eq(1L), any(RatingRequest.class)))
                .thenReturn(response);

        mockMvc.perform(post(basePath + "/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", is("Đánh giá phim thành công.")))
                .andExpect(jsonPath("$.data.ticketId", is(10)))
                .andExpect(jsonPath("$.data.rating", is(Rating.GOOD.ordinal() + 1)));
    }

    @Test
    @DisplayName("GET /my-rating/ticket/{ticketId} - Lấy đánh giá theo vé")
    void testGetRatingByTicket() throws Exception {
        setupAuthentication();

        Long ticketId = 5L;
        RatingResponse response = RatingResponse.builder()
                .ticketId(ticketId)
                .movieTitle("Inception")
                .rating(Rating.EXCELLENT.ordinal() + 1)
                .build();

        Mockito.when(ratingService.getRatingByTicket(1L, ticketId)).thenReturn(response);

        mockMvc.perform(get(basePath + "/my-rating/ticket/" + ticketId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", is("Lấy đánh giá thành công.")))
                .andExpect(jsonPath("$.data.rating", is(Rating.EXCELLENT.ordinal() + 1)));
    }

    @Test
    @DisplayName("GET /all-ratings - Lấy toàn bộ đánh giá cá nhân")
    void testGetAllRatingsByUser() throws Exception {
        setupAuthentication();

        List<RatingResponse> ratings = List.of(
                RatingResponse.builder()
                        .ticketId(1L)
                        .movieTitle("Inception")
                        .rating(Rating.GOOD.ordinal() + 1)
                        .build(),
                RatingResponse.builder()
                        .ticketId(2L)
                        .movieTitle("Batman")
                        .rating(Rating.BAD.ordinal() + 1)
                        .build()
        );

        Mockito.when(ratingService.getRatingsByUser(1L)).thenReturn(ratings);

        mockMvc.perform(get(basePath + "/all-ratings"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", is("Lấy toàn bộ đánh giá cá nhân thành công.")))
                .andExpect(jsonPath("$.data.length()", is(2)));
    }

    @Test
    @DisplayName("GET /my-rating/movie/{movieId} - Lấy đánh giá theo phim")
    void testGetRatingByMovie() throws Exception {
        setupAuthentication();

        Integer movieId = 3;
        List<RatingResponse> responses = List.of(
                RatingResponse.builder()
                        .ticketId(100L)
                        .movieTitle("Tenet")
                        .rating(Rating.VERY_GOOD.ordinal() + 1)
                        .build()
        );

        Mockito.when(ratingService.getRatingsByUserAndMovie(1L, movieId)).thenReturn(responses);

        mockMvc.perform(get(basePath + "/my-rating/movie/" + movieId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", is("Lấy đánh giá cá nhân theo phim thành công.")))
                .andExpect(jsonPath("$.data[0].rating", is(Rating.VERY_GOOD.ordinal() + 1)));
    }
}
