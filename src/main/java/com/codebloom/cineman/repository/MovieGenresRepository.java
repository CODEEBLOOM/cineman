package com.codebloom.cineman.repository;

import com.codebloom.cineman.model.Id.MovieGenresId;
import com.codebloom.cineman.model.MovieGenresEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MovieGenresRepository extends JpaRepository<MovieGenresEntity, MovieGenresId> {
}
