package com.codebloom.cineman.service;

import com.codebloom.cineman.controller.request.MovieVariationRequest;
import com.codebloom.cineman.model.MovieVariationEntity;

import java.util.List;

public interface MovieVariationService {

    List<MovieVariationEntity> findAll();

    MovieVariationEntity findById(Integer id);

    MovieVariationEntity create(MovieVariationRequest request);

    MovieVariationEntity update(Integer id, MovieVariationRequest request);

    void delete(Integer id);
}
