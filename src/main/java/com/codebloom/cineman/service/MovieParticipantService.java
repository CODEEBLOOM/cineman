package com.codebloom.cineman.service;

import com.codebloom.cineman.controller.request.MovieParticipantRequest;
import com.codebloom.cineman.model.MovieParticipantEntity;

public interface MovieParticipantService {

    MovieParticipantEntity addParticipantMovie(MovieParticipantRequest request);
    MovieParticipantEntity updateParticipantMovie(Integer id, MovieParticipantRequest request);
    void deleteParticipantMovie(Integer movieId, Integer participantId);

}
