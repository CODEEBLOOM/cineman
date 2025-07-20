    package com.codebloom.cineman.service.impl;

    import com.codebloom.cineman.controller.request.MovieParticipantRequest;
    import com.codebloom.cineman.exception.DataNotFoundException;
    import com.codebloom.cineman.model.*;
    import com.codebloom.cineman.repository.MovieParticipantRepository;
    import com.codebloom.cineman.service.MovieService;
    import com.codebloom.cineman.service.MovieRoleService;
    import com.codebloom.cineman.service.ParticipantService;

    import org.junit.jupiter.api.BeforeEach;
    import org.junit.jupiter.api.Test;
    import org.mockito.*;

    import java.util.Optional;

    import static org.junit.jupiter.api.Assertions.*;
    import static org.mockito.Mockito.*;

    class MovieParticipantServiceImplTest {

        @Mock
        private MovieParticipantRepository movieParticipantRepository;

        @Mock
        private MovieService movieService;

        @Mock
        private MovieRoleService movieRoleService;

        @Mock
        private ParticipantService participantService;

        @InjectMocks
        private MovieParticipantServiceImpl movieParticipantService;

        @BeforeEach
        void setUp() {
            MockitoAnnotations.openMocks(this);
        }

        @Test
        void addParticipantMovie_shouldSaveSuccessfully() {

            MovieParticipantRequest request = new MovieParticipantRequest();
            request.setParticipantId(1);
            request.setMovieId(2);
            request.setMovieRoleId(3);

            ParticipantEntity participant = new ParticipantEntity();
            participant.setParticipantId(1);

            MovieEntity movie = new MovieEntity();
            movie.setMovieId(2);

            MovieRoleEntity movieRole = new MovieRoleEntity();
            movieRole.setMovieRoleId(3);

            MovieParticipantEntity savedEntity = new MovieParticipantEntity();
            savedEntity.setParticipant(participant);
            savedEntity.setMovie(movie);
            savedEntity.setMovieRole(movieRole);

            when(participantService.findById(1)).thenReturn(participant);
            when(movieService.findById(2, true)).thenReturn(movie);
            when(movieRoleService.findById(3)).thenReturn(movieRole);
            when(movieParticipantRepository.save(any(MovieParticipantEntity.class))).thenReturn(savedEntity);

            MovieParticipantEntity result = movieParticipantService.addParticipantMovie(request);

            assertNotNull(result);
            assertEquals(1, result.getParticipant().getParticipantId());
            assertEquals(2, result.getMovie().getMovieId());
            assertEquals(3, result.getMovieRole().getMovieRoleId());
            verify(movieParticipantRepository).save(any(MovieParticipantEntity.class));
        }

        @Test
        void updateParticipantMovie_shouldUpdateAndReturnEntity() {

            int updateId = 100;
            MovieParticipantRequest request = new MovieParticipantRequest();
            request.setParticipantId(2);
            request.setMovieId(3);
            request.setMovieRoleId(4);

            MovieParticipantEntity existing = new MovieParticipantEntity();
            existing.setId(updateId);
            existing.setParticipant(new ParticipantEntity());
            existing.setMovie(new MovieEntity());
            existing.setMovieRole(new MovieRoleEntity());

            ParticipantEntity updatedParticipant = new ParticipantEntity();
            updatedParticipant.setParticipantId(2);

            MovieEntity updatedMovie = new MovieEntity();
            updatedMovie.setMovieId(3);

            MovieRoleEntity updatedRole = new MovieRoleEntity();
            updatedRole.setMovieRoleId(4);

            when(movieParticipantRepository.findById(updateId)).thenReturn(Optional.of(existing));
            when(participantService.findById(2)).thenReturn(updatedParticipant);
            when(movieService.findById(3, true)).thenReturn(updatedMovie);
            when(movieRoleService.findById(4)).thenReturn(updatedRole);
            when(movieParticipantRepository.save(existing)).thenReturn(existing);

            MovieParticipantEntity result = movieParticipantService.updateParticipantMovie(updateId, request);

            assertNotNull(result);
            assertEquals(2, result.getParticipant().getParticipantId());
            assertEquals(3, result.getMovie().getMovieId());
            assertEquals(4, result.getMovieRole().getMovieRoleId());
            verify(movieParticipantRepository).save(existing);
        }

        @Test
        void updateParticipantMovie_shouldThrowException_whenNotFound() {

            when(movieParticipantRepository.findById(404)).thenReturn(Optional.empty());

            MovieParticipantRequest request = new MovieParticipantRequest();
            request.setParticipantId(1);
            request.setMovieId(2);
            request.setMovieRoleId(3);

            DataNotFoundException exception = assertThrows(DataNotFoundException.class, () ->
                    movieParticipantService.updateParticipantMovie(404, request));

            assertEquals("Participant of Movie not found with id: 404", exception.getMessage());
        }


        @Test
        void deleteParticipantMovie_shouldDeleteIfExists() {

            int movieId = 10;
            int participantId = 20;

            MovieEntity movie = new MovieEntity();
            movie.setMovieId(movieId);

            ParticipantEntity participant = new ParticipantEntity();
            participant.setParticipantId(participantId);

            MovieParticipantEntity entity = new MovieParticipantEntity();

            when(movieService.findById(movieId, true)).thenReturn(movie);
            when(participantService.findById(participantId)).thenReturn(participant);
            when(movieParticipantRepository.findByMovieAndParticipant(movie, participant)).thenReturn(Optional.of(entity));

            movieParticipantService.deleteParticipantMovie(movieId, participantId);

            verify(movieParticipantRepository).delete(entity);
        }

        @Test
        void deleteParticipantMovie_shouldThrowException_whenNotFound() {

            int movieId = 11;
            int participantId = 21;

            MovieEntity movie = new MovieEntity();
            movie.setMovieId(movieId);

            ParticipantEntity participant = new ParticipantEntity();
            participant.setParticipantId(participantId);

            when(movieService.findById(movieId, true)).thenReturn(movie);
            when(participantService.findById(participantId)).thenReturn(participant);
            when(movieParticipantRepository.findByMovieAndParticipant(movie, participant)).thenReturn(Optional.empty());

            DataNotFoundException exception = assertThrows(DataNotFoundException.class, () ->
                    movieParticipantService.deleteParticipantMovie(movieId, participantId));

            assertEquals("Participant of movie not found with id's movie: 11 and  id's participant: 21", exception.getMessage());
        }

    }
