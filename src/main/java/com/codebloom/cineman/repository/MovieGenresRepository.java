package com.codebloom.cineman.repository;

import com.codebloom.cineman.model.MovieGenresEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MovieGenresRepository extends JpaRepository<MovieGenresEntity, Integer> {
}
