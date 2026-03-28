package com.codebloom.cineman.repository;

import com.codebloom.cineman.model.MovieTheaterMappingEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MovieTheaterMappingRepository extends JpaRepository<MovieTheaterMappingEntity, Integer> {

    Page<MovieTheaterMappingEntity> findAllByActiveTrue(Pageable pageable);

    List<MovieTheaterMappingEntity> findAllByActiveTrueAndMovie_MovieId(Integer movieId);

    List<MovieTheaterMappingEntity> findAllByActiveTrueAndMovieTheater_MovieTheaterId(Integer movieTheaterId);

    Optional<MovieTheaterMappingEntity> findByMovieTheaterMappingIdAndActiveTrue(Integer movieTheaterMappingId);

    Optional<MovieTheaterMappingEntity> findByMovie_MovieIdAndMovieTheater_MovieTheaterIdAndActiveTrue(
            Integer movieId,
            Integer movieTheaterId
    );
}
