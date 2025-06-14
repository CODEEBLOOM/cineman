package com.codebloom.cineman.repository;

import com.codebloom.cineman.model.GenresEntity;
import com.codebloom.cineman.model.MovieEntity;
import com.codebloom.cineman.model.MovieGenresEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MovieGenresRepository extends JpaRepository<MovieGenresEntity, Integer> {
    Optional<MovieGenresEntity> findByMovieAndGenres(MovieEntity movieEntity, GenresEntity genresEntity);
}
