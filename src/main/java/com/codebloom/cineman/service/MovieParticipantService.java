package com.codebloom.cineman.service;

import com.codebloom.cineman.controller.request.MovieParticipantRequest;
import com.codebloom.cineman.controller.response.MovieParticipantResponse;
import com.codebloom.cineman.model.MovieParticipantEntity;

import java.util.List;

public interface MovieParticipantService {

    List<MovieParticipantResponse> findAll();
    MovieParticipantEntity addParticipantMovie(MovieParticipantRequest request);
    MovieParticipantEntity updateParticipantMovie(Integer id, MovieParticipantRequest request);
    void deleteParticipantMovie(Integer movieId, Integer participantId);

}
