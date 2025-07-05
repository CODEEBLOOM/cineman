package com.codebloom.cineman.repository;

import com.codebloom.cineman.model.MovieTheaterEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MovieTheaterRepository extends JpaRepository<MovieTheaterEntity,Integer> {
    Optional<MovieTheaterEntity> findByHotline(String hotline);

    Optional<MovieTheaterEntity> findByHotlineAndMovieTheaterIdNot(String hotline, Integer movieTheaterId);

    Page<MovieTheaterEntity> findAllByStatus(Boolean status, Pageable pageable);

    Optional<MovieTheaterEntity>  findByMovieTheaterIdAndStatus(Integer id, Boolean status);
}
