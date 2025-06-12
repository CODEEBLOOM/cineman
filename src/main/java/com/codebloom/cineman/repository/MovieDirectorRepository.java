package com.codebloom.cineman.repository;

import com.codebloom.cineman.model.DirectorEntity;
import com.codebloom.cineman.model.MovieDirectorEntity;
import com.codebloom.cineman.model.MovieEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MovieDirectorRepository extends JpaRepository<MovieDirectorEntity, Integer> {
    Optional<MovieDirectorEntity> findByMovieAndDirector(MovieEntity existingMovie, DirectorEntity exitingDirector);
}
