package com.codebloom.cineman.service.impl;


import com.codebloom.cineman.model.MovieEntity;
import org.springframework.data.domain.Page;


public interface MovieService extends CRUDService<MovieEntity,Integer> {
    Page<MovieEntity> findAll(int page, int size, String sortBy, String sortDir, String searchTerm);
}
