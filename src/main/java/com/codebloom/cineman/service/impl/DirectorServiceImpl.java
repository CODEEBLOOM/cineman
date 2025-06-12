package com.codebloom.cineman.service.impl;

import com.codebloom.cineman.controller.response.DirectorRequest;
import com.codebloom.cineman.exception.DataNotFoundException;
import com.codebloom.cineman.model.DirectorEntity;
import com.codebloom.cineman.repository.DirectorRepository;
import com.codebloom.cineman.repository.MovieDirectorRepository;
import com.codebloom.cineman.service.DirectorService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DirectorServiceImpl implements DirectorService {

    private final DirectorRepository directorRepository;
    private final ModelMapper modelMapper;

    @Override
    public List<DirectorEntity> findAll() {
        return directorRepository.findAll();
    }

    @Override
    public DirectorEntity findById(Integer id) {
         return directorRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("Director not found with id: " + id));
    }

    @Override
    @Transactional
    public DirectorEntity save(DirectorRequest director) {
        DirectorEntity directorEntity = modelMapper.map(director, DirectorEntity.class);
        return directorRepository.save(directorEntity);
    }

    @Override
    public DirectorEntity update(Integer id, DirectorRequest director) {
        DirectorEntity directorEntity = findById(id);

        directorEntity.setFullname(director.getFullname());
        directorEntity.setNickname(director.getNickname());
        directorEntity.setGender(director.getGender());
        directorEntity.setNationality(director.getNationality());
        directorEntity.setMiniBio(director.getMiniBio());
        directorEntity.setAvatar(director.getAvatar());

        return directorRepository.save(directorEntity);
    }

    @Override
    public int delete(Integer id) {
        if(directorRepository.isDirectorNotHaveMovie(id)){
            directorRepository.deleteById(id);
            return 1;
        }
        return -1;
    }
}
