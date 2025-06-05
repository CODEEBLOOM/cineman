package com.codebloom.cineman.repository;

import com.codebloom.cineman.model.MovieTheatersEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MovieTheatersRepository extends JpaRepository<MovieTheatersEntity,Integer> {
}
