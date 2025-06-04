package com.codebloom.cineman.repository;

import com.codebloom.cineman.model.MovieTheatersEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MovieTheatersRepository extends JpaRepository<MovieTheatersEntity,Integer> {
}
