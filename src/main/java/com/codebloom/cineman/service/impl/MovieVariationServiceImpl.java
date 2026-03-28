package com.codebloom.cineman.service.impl;

import com.codebloom.cineman.controller.request.MovieVariationRequest;
import com.codebloom.cineman.exception.DataExistingException;
import com.codebloom.cineman.exception.DataNotFoundException;
import com.codebloom.cineman.model.MovieVariationEntity;
import com.codebloom.cineman.repository.MovieVariationRepository;
import com.codebloom.cineman.service.MovieVariationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j(topic = "MOVIE-VARIATION-SERVICE")
@RequiredArgsConstructor
public class MovieVariationServiceImpl implements MovieVariationService {

    private final MovieVariationRepository movieVariationRepository;

    @Override
    @Transactional
    public MovieVariationEntity create(MovieVariationRequest request) {
        String normalizedName = request.getName().trim();
        MovieVariationEntity existingMovieVariation = movieVariationRepository.findByName(normalizedName).orElse(null);
        if (existingMovieVariation != null) {
            if (Boolean.TRUE.equals(existingMovieVariation.getStatus())) {
                throw new DataExistingException("Movie variation already exists with name: " + normalizedName);
            }
            existingMovieVariation.setName(normalizedName);
            existingMovieVariation.setStatus(true);
            return movieVariationRepository.save(existingMovieVariation);
        }

        MovieVariationEntity movieVariationEntity = MovieVariationEntity.builder()
                .name(normalizedName)
                .status(true)
                .build();
        return movieVariationRepository.save(movieVariationEntity);
    }

    @Override
    @Transactional
    public MovieVariationEntity update(Integer id, MovieVariationRequest request) {
        MovieVariationEntity movieVariationEntity = findById(id);
        String normalizedName = request.getName().trim();
        MovieVariationEntity existingMovieVariation = movieVariationRepository.findByName(normalizedName).orElse(null);
        if (existingMovieVariation != null && !existingMovieVariation.getId().equals(id)) {
            throw new DataExistingException("Movie variation already exists with name: " + normalizedName);
        }
        movieVariationEntity.setName(normalizedName);
        movieVariationEntity.setStatus(true);
        return movieVariationRepository.save(movieVariationEntity);
    }

    @Override
    public List<MovieVariationEntity> findAll() {
        return movieVariationRepository.findAllByStatus(true);
    }

    @Override
    public MovieVariationEntity findById(Integer id) {
        return movieVariationRepository.findByIdAndStatus(id, true)
                .orElseThrow(() -> new DataNotFoundException("Movie variation not found with id: " + id));
    }

    @Override
    @Transactional
    public void delete(Integer id) {
        MovieVariationEntity movieVariationEntity = findById(id);
        movieVariationEntity.setStatus(false);
        movieVariationRepository.save(movieVariationEntity);
    }
}
