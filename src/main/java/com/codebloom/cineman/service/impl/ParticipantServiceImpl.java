package com.codebloom.cineman.service.impl;

import com.codebloom.cineman.controller.request.ParticipantRequest;
import com.codebloom.cineman.exception.DataNotFoundException;
import com.codebloom.cineman.model.MovieParticipantEntity;
import com.codebloom.cineman.repository.MovieParticipantRepository;
import com.codebloom.cineman.repository.ParticipantRepository;
import com.codebloom.cineman.service.ParticipantService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.codebloom.cineman.model.ParticipantEntity;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ParticipantServiceImpl implements ParticipantService {

    private final ParticipantRepository directorRepository;
    private final MovieParticipantRepository movieParticipantRepository;
    private final ModelMapper modelMapper;

    @Override
    public List<ParticipantEntity> findAll() {
        return directorRepository.findAll();
    }

    @Override
    public ParticipantEntity findById(Integer id) {
         return directorRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("Director not found with id: " + id));
    }

    @Override
    @Transactional
    public ParticipantEntity save(ParticipantRequest director) {
        ParticipantEntity participantEntity = modelMapper.map(director, ParticipantEntity.class);
        return directorRepository.save(participantEntity);
    }

    @Override
    public ParticipantEntity update(Integer id, ParticipantRequest director) {
        ParticipantEntity participantEntity = findById(id);

        participantEntity.setBirthName(director.getBirthName());
        participantEntity.setNickname(director.getNickname());
        participantEntity.setGender(director.getGender());
        participantEntity.setNationality(director.getNationality());
        participantEntity.setMiniBio(director.getMiniBio());
        participantEntity.setAvatar(director.getAvatar());

        return directorRepository.save(participantEntity);
    }

    @Override
    public int delete(Integer id) {
        if(!movieParticipantRepository.existsByParticipantId(id)){
            directorRepository.deleteById(id);
            return 1;
        }
        return -1;
    }
}
