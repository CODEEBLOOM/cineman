package com.codebloom.cineman.service.impl;

import com.codebloom.cineman.controller.request.GenresRequest;
import com.codebloom.cineman.exception.DataExistingException;
import com.codebloom.cineman.exception.DataNotFoundException;
import com.codebloom.cineman.model.GenresEntity;
import com.codebloom.cineman.repository.GenresRepository;
import com.codebloom.cineman.service.GenreService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GenreServiceImple implements GenreService {

    private final GenresRepository genresRepository;
    private final ModelMapper modelMapper;

    @Override
    public GenresEntity create(GenresRequest genres) {
        genresRepository.findByName(genres.getName()).ifPresent(genre -> {
            throw new DataExistingException("Genre with name " + genres.getName() + " already exists");
        });
        return genresRepository.save(modelMapper.map(genres, GenresEntity.class));
    }

    @Override
    public GenresEntity update(Integer id, GenresRequest genres) {
        GenresEntity existGenre =  this.findById(id);
        modelMapper.map(genres, existGenre);
        return genresRepository.save(existGenre);
    }

    @Override
    public int delete(Integer id) {
        this.findById(id);
        if(!genresRepository.existsByGenreId(id)) {
            genresRepository.deleteById(id);
            return 1;
        }
        return -1;
    }

    @Override
    public GenresEntity findById(Integer id) {
        return genresRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("Genre not found with id: " + id));
    }

    @Override
    public List<GenresEntity> findAll() {
        return genresRepository.findAll();
    }
}
