package com.codebloom.cineman.service;

import com.codebloom.cineman.controller.request.MovieVariationRequest;
import com.codebloom.cineman.model.MovieVariationEntity;

import java.util.List;

public interface MovieVariationService {

    MovieVariationEntity create(MovieVariationRequest request);

    MovieVariationEntity update(Integer id, MovieVariationRequest request);

    List<MovieVariationEntity> findAll();

    MovieVariationEntity findById(Integer id);

    void delete(Integer id);
}
