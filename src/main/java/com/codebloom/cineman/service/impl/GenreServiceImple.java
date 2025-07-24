package com.codebloom.cineman.service.impl;

import com.codebloom.cineman.controller.request.GenresRequest;
import com.codebloom.cineman.exception.*;
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
        GenresEntity genresEntity = modelMapper.map(genres, GenresEntity.class);
        genresEntity.setActive(true);
        return genresRepository.save(genresEntity);
    }

    @Override
    public GenresEntity update(Integer id, GenresRequest genres) {
        GenresEntity existGenre =  this.findById(id);
        modelMapper.map(genres, existGenre);
        return genresRepository.save(existGenre);
    }

    @Override
    public void delete(Integer id) {
        GenresEntity genre = this.findById(id);
        genre.setActive(false);
        genresRepository.save(genre);
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
