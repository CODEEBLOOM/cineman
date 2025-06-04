package com.codebloom.cineman.repository;

import com.codebloom.cineman.model.Id.MovieCastId;
import com.codebloom.cineman.model.MovieCastEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MovieCastRepository extends JpaRepository<MovieCastEntity, MovieCastId> {
}

