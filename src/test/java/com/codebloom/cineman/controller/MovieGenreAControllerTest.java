package com.codebloom.cineman.controller;

import com.codebloom.cineman.TestSecurityConfig;
import com.codebloom.cineman.controller.admin.MovieGenreAController;
import com.codebloom.cineman.controller.request.MovieGenreRequest;
import com.codebloom.cineman.model.GenresEntity;
import com.codebloom.cineman.model.MovieEntity;
import com.codebloom.cineman.model.MovieGenresEntity;
import com.codebloom.cineman.repository.PermissionRepository;
import com.codebloom.cineman.service.JwtService;
import com.codebloom.cineman.service.MovieGenreService;
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

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@TestPropertySource(properties = "api.path=/api/v1")
@WebMvcTest(controllers = MovieGenreAController.class)
@Import(TestSecurityConfig.class)
class MovieGenreAControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private PermissionRepository permissionRepository;

    @MockBean
    private MovieGenreService movieGenreService;

    @Autowired
    private ObjectMapper objectMapper;

    private final String BASE_PATH = "/api/v1/admin/movie-genre";

    @Test
    @DisplayName("POST /add - Gán thể loại cho phim")
    void testAddMovieGenre() throws Exception {

        MovieGenreRequest request = new MovieGenreRequest();
        request.setMovieId(1);
        request.setGenreId(2);

        MovieEntity movie = new MovieEntity();
        movie.setMovieId(1);

        GenresEntity genre = new GenresEntity();
        genre.setGenresId(2);

        MovieGenresEntity responseEntity = new MovieGenresEntity();
        responseEntity.setMovie(movie);
        responseEntity.setGenres(genre);

        when(movieGenreService.addMovieGenre(any(MovieGenreRequest.class))).thenReturn(responseEntity);

        mockMvc.perform(post(BASE_PATH + "/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("Add genre for movie success"))
                .andExpect(jsonPath("$.data.movie.movieId").value(1))        // Kiểm tra movieId
                .andExpect(jsonPath("$.data.genres.genresId").value(2));    // Kiểm tra genresId
    }



    @Test
    @DisplayName("PUT /genre/{id}/update - Cập nhật thể loại cho phim")
    void testUpdateMovieGenre() throws Exception {
        MovieGenreRequest request = new MovieGenreRequest();
        request.setMovieId(1);
        request.setGenreId(2);

        MovieEntity movie = new MovieEntity();
        movie.setMovieId(1);

        GenresEntity genre = new GenresEntity();
        genre.setGenresId(2);

        MovieGenresEntity responseEntity = new MovieGenresEntity();
        responseEntity.setMovie(movie);
        responseEntity.setGenres(genre);

        when(movieGenreService.updateGenreOfMovie(any(MovieGenreRequest.class), eq(2))).thenReturn(responseEntity);

        mockMvc.perform(put(BASE_PATH + "/genre/2/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Update genre for movie success"))
                .andExpect(jsonPath("$.data.movie.movieId").value(1))
                .andExpect(jsonPath("$.data.genres.genresId").value(2));
    }


    @Test
    @DisplayName("DELETE /{movieId}/{genreId}/delete - Xóa thể loại của phim")
    void testDeleteMovieGenre() throws Exception {
        MovieGenreRequest request = new MovieGenreRequest();
        request.setMovieId(1);
        request.setGenreId(2);

        doNothing().when(movieGenreService).deleteMovieGenre(any(MovieGenreRequest.class));

        mockMvc.perform(delete(BASE_PATH + "/1/2/delete"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Delete genre for movie success"))
                .andExpect(jsonPath("$.data").doesNotExist());
    }
}
