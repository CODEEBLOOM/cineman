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

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j(topic = "MOVIE-VARIATION-SERVICE")
public class MovieVariationServiceImpl implements MovieVariationService {

    private final MovieVariationRepository movieVariationRepository;

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
    public MovieVariationEntity create(MovieVariationRequest request) {
        movieVariationRepository.findByNameAndStatus(request.getName().trim(), true)
                .ifPresent(movieVariation -> {
                    throw new DataExistingException("Movie variation already exists with name: " + request.getName());
                });

        MovieVariationEntity movieVariation = MovieVariationEntity.builder()
                .name(request.getName().trim())
                .status(true)
                .build();
        return movieVariationRepository.save(movieVariation);
    }

    @Override
    public MovieVariationEntity update(Integer id, MovieVariationRequest request) {
        MovieVariationEntity movieVariation = movieVariationRepository.findByIdAndStatus(id, true)
                .orElseThrow(() -> new DataNotFoundException("Movie variation not found with id: " + id));

        movieVariationRepository.findByNameAndStatusAndIdNot(request.getName().trim(), true, id)
                .ifPresent(existingMovieVariation -> {
                    throw new DataExistingException("Movie variation already exists with name: " + request.getName());
                });

        movieVariation.setName(request.getName().trim());
        movieVariation.setStatus(true);
        return movieVariationRepository.save(movieVariation);
    }

    @Override
    public void delete(Integer id) {
        MovieVariationEntity movieVariation = movieVariationRepository.findByIdAndStatus(id, true)
                .orElseThrow(() -> new DataNotFoundException("Movie variation not found with id: " + id));
        movieVariation.setStatus(false);
        movieVariationRepository.save(movieVariation);
    }
}
