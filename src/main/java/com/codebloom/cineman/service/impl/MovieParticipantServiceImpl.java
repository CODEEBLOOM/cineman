package com.codebloom.cineman.service.impl;

import com.codebloom.cineman.controller.request.MovieParticipantRequest;
import com.codebloom.cineman.Exception.*;
import com.codebloom.cineman.model.MovieRoleEntity;
import com.codebloom.cineman.model.ParticipantEntity;
import com.codebloom.cineman.model.MovieParticipantEntity;
import com.codebloom.cineman.model.MovieEntity;
import com.codebloom.cineman.repository.MovieParticipantRepository;
import com.codebloom.cineman.service.MovieParticipantService;
import com.codebloom.cineman.service.MovieRoleService;
import com.codebloom.cineman.service.ParticipantService;
import com.codebloom.cineman.service.MovieService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MovieParticipantServiceImpl implements MovieParticipantService {

    private final MovieParticipantRepository movieParticipantRepository;
    private final MovieService movieService;
    private final MovieRoleService movieRoleService;
    private final ParticipantService participantService;

    @Override
    public MovieParticipantEntity addParticipantMovie(MovieParticipantRequest request) {
        ParticipantEntity exitingParticipant = participantService.findById(request.getParticipantId());
        MovieEntity existingMovie = movieService.findById(request.getMovieId(), true);
        MovieRoleEntity existingMovieRole = movieRoleService.findById(request.getMovieRoleId());

        MovieParticipantEntity modifiedDirector = new MovieParticipantEntity();
        modifiedDirector.setParticipant(exitingParticipant);
        modifiedDirector.setMovie(existingMovie);
        modifiedDirector.setMovieRole(existingMovieRole);

        return movieParticipantRepository.save(modifiedDirector);
    }

    @Override
    public MovieParticipantEntity updateParticipantMovie(Integer id, MovieParticipantRequest request) {
        MovieParticipantEntity existMovieParticipant = movieParticipantRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("Participant of Movie not found with id: " + id));

        ParticipantEntity exitingParticipant = participantService.findById(request.getParticipantId());
        MovieEntity existingMovie = movieService.findById(request.getMovieId(), true);
        MovieRoleEntity existingMovieRole = movieRoleService.findById(request.getMovieRoleId());

        // Set lại dữ liệu mới //
        existMovieParticipant.setParticipant(exitingParticipant);
        existMovieParticipant.setMovie(existingMovie);
        existMovieParticipant.setMovieRole(existingMovieRole);

        return movieParticipantRepository.save(existMovieParticipant);
    }

    @Override
    public void deleteParticipantMovie(Integer movieId, Integer participantId) {
        ParticipantEntity exitingParticipant = participantService.findById(participantId);
        MovieEntity existingMovie = movieService.findById(movieId, true);

        MovieParticipantEntity existMovieParticipant = movieParticipantRepository.findByMovieAndParticipant(existingMovie, exitingParticipant)
                .orElseThrow(() -> new DataNotFoundException("Participant of movie not found with id's movie: " + movieId + " and  id's participant: " + participantId));
        movieParticipantRepository.delete(existMovieParticipant);
    }
}
