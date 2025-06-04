package com.codebloom.cineman.repository;

import com.codebloom.cineman.model.MovieEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MovieRepository extends JpaRepository<MovieEntity,Integer> {
}
