package com.codebloom.cineman.repository;

import com.codebloom.cineman.model.Id.MovieDirectorId;
import com.codebloom.cineman.model.MovieDirectorEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MovieDirectorRepository extends JpaRepository<MovieDirectorEntity, MovieDirectorId> {
}
