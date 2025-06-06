package com.codebloom.cineman.repository;

import com.codebloom.cineman.model.MovieStatusEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MovieStatusRepository extends JpaRepository<MovieStatusEntity,Integer> {
    Optional<MovieStatusEntity> findByName(String name);
}
