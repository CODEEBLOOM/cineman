package com.codebloom.cineman.service.impl;

import com.codebloom.cineman.common.constant.MovieStatus;
import com.codebloom.cineman.controller.request.MovieCreationRequest;
import com.codebloom.cineman.controller.request.MoviePageQueryRequest;
import com.codebloom.cineman.controller.request.MovieUpdateRequest;
import com.codebloom.cineman.controller.response.MoviePageableResponse;
import com.codebloom.cineman.controller.response.MovieResponse;
import com.codebloom.cineman.model.GenresEntity;
import com.codebloom.cineman.model.MovieEntity;
import com.codebloom.cineman.model.MovieParticipantEntity;
import com.codebloom.cineman.model.MovieRoleEntity;
import com.codebloom.cineman.model.MovieStatusEntity;
import com.codebloom.cineman.model.ParticipantEntity;
import com.codebloom.cineman.repository.MovieGenresRepository;
import com.codebloom.cineman.repository.MovieParticipantRepository;
import com.codebloom.cineman.repository.MovieRepository;
import com.codebloom.cineman.repository.MovieRoleRepository;
import com.codebloom.cineman.repository.MovieStatusRepository;
import com.codebloom.cineman.service.GenreService;
import com.codebloom.cineman.service.MovieStatusService;
import com.codebloom.cineman.service.ParticipantService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MovieServiceImplTest {

    @Mock
    private MovieRepository movieRepository;

    @Mock
    private MovieStatusRepository movieStatusRepository;

    @Mock
    private MovieStatusService movieStatusService;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private GenreService genreService;

    @Mock
    private MovieGenresRepository movieGenresRepository;

    @Mock
    private MovieParticipantRepository movieParticipantRepository;

    @Mock
    private MovieRoleRepository movieRoleRepository;

    @Mock
    private ParticipantService participantService;

    @InjectMocks
    private MovieServiceImpl movieService;

    @Test
    void findById_shouldMapVietnameseRoleNamesIntoDirectorsAndCasts() {
        MovieEntity movie = MovieEntity.builder()
                .movieId(3)
                .title("Movie 3")
                .status(MovieStatusEntity.builder()
                        .statusId(MovieStatus.MOVIE_STATUS_SC)
                        .name("Sap chieu")
                        .active(true)
                        .build())
                .movieGenres(Collections.emptySet())
                .movieParticipants(buildMovieParticipants(
                        participant(1, "Actor 1"), role(1, "Diễn Viên"),
                        participant(2, "Director 1"), role(2, "Đạo Diễn"),
                        participant(3, "Actor 2"), role(1, "dien vien"),
                        participant(4, "Director 2"), role(2, "director")))
                .build();

        when(movieRepository.findById(3)).thenReturn(Optional.of(movie));

        MovieResponse response = movieService.findById(3);

        assertThat(response.getCasts())
                .extracting(ParticipantEntity::getParticipantId)
                .containsExactly(1, 3);
        assertThat(response.getDirectors())
                .extracting(ParticipantEntity::getParticipantId)
                .containsExactly(2, 4);
    }

    @Test
    void save_shouldPersistDirectorsAndCastsFromRequest() {
        MovieCreationRequest request = new MovieCreationRequest(
                "Movie 10",
                "Synopsis",
                "Detail",
                new Date(),
                new Date(),
                "VN",
                120,
                16,
                MovieStatus.MOVIE_STATUS_SC,
                List.of(1),
                List.of(11),
                List.of(21),
                "https://example.com/trailer",
                "poster.jpg",
                "banner.jpg"
        );

        MovieEntity movie = MovieEntity.builder().movieId(10).build();
        when(movieStatusRepository.findById(MovieStatus.MOVIE_STATUS_SC))
                .thenReturn(Optional.of(MovieStatusEntity.builder().statusId(MovieStatus.MOVIE_STATUS_SC).active(true).build()));
        when(modelMapper.map(request, MovieEntity.class)).thenReturn(movie);
        when(movieRepository.save(any(MovieEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(genreService.findById(1)).thenReturn(genre(1));
        when(movieRoleRepository.findAllByActive(true)).thenReturn(List.of(role(1, "director"), role(2, "actor")));
        when(participantService.findById(11)).thenReturn(participant(11, "Director 11"));
        when(participantService.findById(21)).thenReturn(participant(21, "Actor 21"));

        MovieEntity savedMovie = movieService.save(request);

        assertThat(savedMovie.getMovieParticipants())
                .extracting(mp -> mp.getParticipant().getParticipantId())
                .containsExactly(11, 21);
        verify(movieParticipantRepository, times(2)).save(any(MovieParticipantEntity.class));
        verify(movieGenresRepository).save(any());
    }

    @Test
    void update_shouldReplaceDirectorsAndCastsFromRequest() {
        MovieEntity movie = MovieEntity.builder()
                .movieId(10)
                .title("Old title")
                .status(MovieStatusEntity.builder().statusId(MovieStatus.MOVIE_STATUS_DB).active(true).build())
                .movieGenres(Collections.emptySet())
                .movieParticipants(Collections.emptySet())
                .build();

        MovieUpdateRequest request = new MovieUpdateRequest(
                10,
                "New title",
                "New synopsis",
                "New detail",
                new Date(),
                new Date(),
                "EN",
                130,
                18,
                MovieStatus.MOVIE_STATUS_SC,
                List.of(2),
                List.of(31),
                List.of(41),
                "https://example.com/new-trailer",
                "new-poster.jpg",
                "new-banner.jpg"
        );

        when(movieRepository.findById(10)).thenReturn(Optional.of(movie));
        when(movieStatusRepository.findById(MovieStatus.MOVIE_STATUS_SC))
                .thenReturn(Optional.of(MovieStatusEntity.builder().statusId(MovieStatus.MOVIE_STATUS_SC).active(true).build()));
        when(movieRepository.save(any(MovieEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(genreService.findById(2)).thenReturn(genre(2));
        when(movieRoleRepository.findAllByActive(true)).thenReturn(List.of(role(1, "director"), role(2, "cast")));
        when(participantService.findById(31)).thenReturn(participant(31, "Director 31"));
        when(participantService.findById(41)).thenReturn(participant(41, "Actor 41"));

        MovieResponse response = movieService.update(request);

        assertThat(response.getDirectors())
                .extracting(ParticipantEntity::getParticipantId)
                .containsExactly(31);
        assertThat(response.getCasts())
                .extracting(ParticipantEntity::getParticipantId)
                .containsExactly(41);
        verify(movieParticipantRepository).deleteAllByMovie(movie);
        verify(movieParticipantRepository).flush();
        verify(movieGenresRepository).deleteAllByMovie(movie);
        verify(movieGenresRepository).flush();
    }

    @Test
    void findAllByPageAndFilter_shouldUseMappingForUpcomingMovies() {
        MoviePageQueryRequest request = new MoviePageQueryRequest();
        request.setPage(0);
        request.setSize(8);
        request.setStatus(MovieStatus.MOVIE_STATUS_SC);

        MovieStatusEntity movieStatus = MovieStatusEntity.builder()
                .statusId(MovieStatus.MOVIE_STATUS_SC)
                .active(true)
                .build();
        MovieEntity movie = MovieEntity.builder()
                .movieId(1)
                .title("Upcoming")
                .status(movieStatus)
                .movieGenres(Collections.emptySet())
                .movieParticipants(Collections.emptySet())
                .build();
        Page<MovieEntity> page = new PageImpl<>(List.of(movie), PageRequest.of(0, 8), 1);

        when(movieStatusService.findById(MovieStatus.MOVIE_STATUS_SC)).thenReturn(movieStatus);
        when(movieRepository.findAllByReleaseDateGreaterThanEqualAndStatusAndMovieTheaterMapping(
                any(Date.class),
                eq(movieStatus),
                eq(7),
                any(PageRequest.class)
        )).thenReturn(page);

        MoviePageableResponse response = movieService.findAllByPageAndFilter(request, 7);

        assertThat(response.getMovies())
                .extracting(MovieResponse::getMovieId)
                .containsExactly(1);
    }

    private Set<MovieParticipantEntity> buildMovieParticipants(
            ParticipantEntity participant1,
            MovieRoleEntity role1,
            ParticipantEntity participant2,
            MovieRoleEntity role2,
            ParticipantEntity participant3,
            MovieRoleEntity role3,
            ParticipantEntity participant4,
            MovieRoleEntity role4
    ) {
        Set<MovieParticipantEntity> participants = new LinkedHashSet<>();
        participants.add(movieParticipant(participant1, role1));
        participants.add(movieParticipant(participant2, role2));
        participants.add(movieParticipant(participant3, role3));
        participants.add(movieParticipant(participant4, role4));
        return participants;
    }

    private MovieParticipantEntity movieParticipant(ParticipantEntity participant, MovieRoleEntity role) {
        MovieParticipantEntity movieParticipant = new MovieParticipantEntity();
        movieParticipant.setParticipant(participant);
        movieParticipant.setMovieRole(role);
        return movieParticipant;
    }

    private ParticipantEntity participant(Integer id, String nickname) {
        return ParticipantEntity.builder()
                .participantId(id)
                .birthName(nickname)
                .nickname(nickname)
                .active(true)
                .build();
    }

    private MovieRoleEntity role(Integer id, String name) {
        return MovieRoleEntity.builder()
                .movieRoleId(id)
                .name(name)
                .active(true)
                .build();
    }

    private GenresEntity genre(Integer id) {
        GenresEntity genre = new GenresEntity();
        genre.setGenresId(id);
        return genre;
    }
}
