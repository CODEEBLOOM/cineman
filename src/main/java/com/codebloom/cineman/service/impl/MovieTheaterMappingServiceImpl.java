package com.codebloom.cineman.service.impl;

import com.codebloom.cineman.controller.request.MovieTheaterMappingRequest;
import com.codebloom.cineman.controller.request.PageRequest;
import com.codebloom.cineman.controller.response.MetaResponse;
import com.codebloom.cineman.controller.response.MovieTheaterMappingPage;
import com.codebloom.cineman.controller.response.MovieTheaterMappingResponse;
import com.codebloom.cineman.exception.DataExistingException;
import com.codebloom.cineman.exception.DataNotFoundException;
import com.codebloom.cineman.model.MovieEntity;
import com.codebloom.cineman.model.MovieTheaterEntity;
import com.codebloom.cineman.model.MovieTheaterMappingEntity;
import com.codebloom.cineman.repository.MovieRepository;
import com.codebloom.cineman.repository.MovieTheaterMappingRepository;
import com.codebloom.cineman.repository.MovieTheaterRepository;
import com.codebloom.cineman.service.MovieTheaterMappingService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MovieTheaterMappingServiceImpl implements MovieTheaterMappingService {

    private final MovieTheaterMappingRepository movieTheaterMappingRepository;
    private final MovieRepository movieRepository;
    private final MovieTheaterRepository movieTheaterRepository;

    @Override
    public MovieTheaterMappingPage findAllByPage(PageRequest pageRequest) {
        Page<MovieTheaterMappingEntity> page = movieTheaterMappingRepository.findAllByActiveTrue(
                org.springframework.data.domain.PageRequest.of(pageRequest.getPage(), pageRequest.getSize())
        );
        List<MovieTheaterMappingResponse> list = page.getContent()
                .stream()
                .map(this::convert)
                .toList();
        MetaResponse meta = MetaResponse.builder()
                .totalElements((int) page.getTotalElements())
                .totalPages(page.getTotalPages())
                .pageSize(page.getSize())
                .currentPage(page.getNumber())
                .build();
        return MovieTheaterMappingPage.builder()
                .movieTheaterMappings(list)
                .meta(meta)
                .build();
    }

    @Override
    public List<MovieTheaterMappingResponse> findAllByMovieId(Integer movieId) {
        movieRepository.findById(movieId)
                .orElseThrow(() -> new DataNotFoundException("Movie not found with id: " + movieId));
        return movieTheaterMappingRepository.findAllByActiveTrueAndMovie_MovieId(movieId)
                .stream()
                .map(this::convert)
                .toList();
    }

    @Override
    public List<MovieTheaterMappingResponse> findAllByMovieTheaterId(Integer movieTheaterId) {
        movieTheaterRepository.findByMovieTheaterIdAndStatus(movieTheaterId, true)
                .orElseThrow(() -> new DataNotFoundException("Movie theater not found with id: " + movieTheaterId));
        return movieTheaterMappingRepository.findAllByActiveTrueAndMovieTheater_MovieTheaterId(movieTheaterId)
                .stream()
                .map(this::convert)
                .toList();
    }

    @Override
    public MovieTheaterMappingResponse findById(Integer id) {
        return movieTheaterMappingRepository.findByMovieTheaterMappingIdAndActiveTrue(id)
                .map(this::convert)
                .orElseThrow(() -> new DataNotFoundException("Movie theater mapping not found with id: " + id));
    }

    @Override
    @Transactional
    public MovieTheaterMappingResponse save(MovieTheaterMappingRequest request) {
        ensureActiveMappingNotExists(request.getMovieId(), request.getMovieTheaterId(), null);

        MovieEntity movie = movieRepository.findById(request.getMovieId())
                .orElseThrow(() -> new DataNotFoundException("Movie not found with id: " + request.getMovieId()));
        MovieTheaterEntity movieTheater = movieTheaterRepository
                .findByMovieTheaterIdAndStatus(request.getMovieTheaterId(), true)
                .orElseThrow(() -> new DataNotFoundException("Movie theater not found with id: " + request.getMovieTheaterId()));

        MovieTheaterMappingEntity entity = MovieTheaterMappingEntity.builder()
                .movie(movie)
                .movieTheater(movieTheater)
                .active(true)
                .build();
        return convert(movieTheaterMappingRepository.save(entity));
    }

    @Override
    @Transactional
    public MovieTheaterMappingResponse update(Integer id, MovieTheaterMappingRequest request) {
        MovieTheaterMappingEntity entity = movieTheaterMappingRepository
                .findByMovieTheaterMappingIdAndActiveTrue(id)
                .orElseThrow(() -> new DataNotFoundException("Movie theater mapping not found with id: " + id));

        ensureActiveMappingNotExists(request.getMovieId(), request.getMovieTheaterId(), id);

        MovieEntity movie = movieRepository.findById(request.getMovieId())
                .orElseThrow(() -> new DataNotFoundException("Movie not found with id: " + request.getMovieId()));
        MovieTheaterEntity movieTheater = movieTheaterRepository
                .findByMovieTheaterIdAndStatus(request.getMovieTheaterId(), true)
                .orElseThrow(() -> new DataNotFoundException("Movie theater not found with id: " + request.getMovieTheaterId()));

        entity.setMovie(movie);
        entity.setMovieTheater(movieTheater);
        entity.setActive(true);
        return convert(movieTheaterMappingRepository.save(entity));
    }

    @Override
    @Transactional
    public void delete(Integer id) {
        MovieTheaterMappingEntity entity = movieTheaterMappingRepository
                .findByMovieTheaterMappingIdAndActiveTrue(id)
                .orElseThrow(() -> new DataNotFoundException("Movie theater mapping not found with id: " + id));
        entity.setActive(false);
        movieTheaterMappingRepository.save(entity);
    }

    private void ensureActiveMappingNotExists(Integer movieId, Integer movieTheaterId, Integer currentId) {
        movieTheaterMappingRepository
                .findByMovie_MovieIdAndMovieTheater_MovieTheaterIdAndActiveTrue(movieId, movieTheaterId)
                .ifPresent(existing -> {
                    if (currentId == null || !existing.getMovieTheaterMappingId().equals(currentId)) {
                        throw new DataExistingException("Movie theater mapping already exists");
                    }
                });
    }

    private MovieTheaterMappingResponse convert(MovieTheaterMappingEntity entity) {
        return MovieTheaterMappingResponse.builder()
                .movieTheaterMappingId(entity.getMovieTheaterMappingId())
                .movieId(entity.getMovie().getMovieId())
                .movieTitle(entity.getMovie().getTitle())
                .movieStatus(entity.getMovie().getStatus() != null ? entity.getMovie().getStatus().getStatusId() : null)
                .movieTheaterId(entity.getMovieTheater().getMovieTheaterId())
                .movieTheaterName(entity.getMovieTheater().getName())
                .active(entity.getActive())
                .build();
    }
}
