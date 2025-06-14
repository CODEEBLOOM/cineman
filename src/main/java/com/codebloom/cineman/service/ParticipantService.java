package com.codebloom.cineman.service;

import com.codebloom.cineman.controller.request.ParticipantRequest;
import com.codebloom.cineman.model.ParticipantEntity;

import java.util.List;

public interface ParticipantService {

    List<ParticipantEntity> findAll();
    ParticipantEntity findById(Integer id);
    ParticipantEntity save(ParticipantRequest director);
    ParticipantEntity update(Integer id, ParticipantRequest director);
    int delete(Integer id);

}
