package com.codebloom.cineman.service.impl;

import com.codebloom.cineman.controller.request.MovieRoleRequest;
import com.codebloom.cineman.exception.DataExistingException;
import com.codebloom.cineman.exception.DataNotFoundException;
import com.codebloom.cineman.model.MovieRoleEntity;
import com.codebloom.cineman.repository.MovieRoleRepository;
import com.codebloom.cineman.service.MovieRoleService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@RequiredArgsConstructor
public class MovieRoleServiceImpl implements MovieRoleService {

    private final MovieRoleRepository movieRoleRepository;
    private final ModelMapper modelMapper;

    @Override
    public List<MovieRoleEntity> findAll() {
        return movieRoleRepository.findAllByActive(true);
    }

    @Override
    public MovieRoleEntity findById(Integer id) {
        return movieRoleRepository.findByMovieRoleIdAndActive(id, true)
                .orElseThrow(() -> new DataNotFoundException("Movie Role Not Found with id: " + id));
    }

    @Override
    public MovieRoleEntity create(MovieRoleRequest movieRole) {
        movieRoleRepository.findByNameAndActive(movieRole.getName().trim(), true)
                .ifPresent((role) -> {throw new DataExistingException("Movie Role already exist with name: " + movieRole.getName());
                });
        movieRole.setName(movieRole.getName().trim());
        MovieRoleEntity movieRoleEntity = modelMapper.map(movieRole, MovieRoleEntity.class);
        movieRoleEntity.setActive(true);
        return movieRoleRepository.save(movieRoleEntity);
    }

    @Override
    public MovieRoleEntity update(Integer id, MovieRoleRequest movieRole) {
        MovieRoleEntity existMovieRoleEntity =  this.findById(id);
        existMovieRoleEntity.setName(movieRole.getName());
        existMovieRoleEntity.setDescription(movieRole.getDescription());
        return movieRoleRepository.save(existMovieRoleEntity);
    }

    @Override
    public void delete(Integer movieRoleId) {
        MovieRoleEntity movieRole = this.findById(movieRoleId);
        movieRole.setActive(false);
        movieRoleRepository.save(movieRole);
    }
}
