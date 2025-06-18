package com.codebloom.cineman.service;

import com.codebloom.cineman.controller.request.GenresRequest;
import com.codebloom.cineman.model.GenresEntity;

import java.util.List;

public interface GenreService {

    GenresEntity create(GenresRequest genres);
    GenresEntity update(Integer id, GenresRequest genres);
    void delete(Integer id);
    GenresEntity findById(Integer id);
    List<GenresEntity> findAll();

}
