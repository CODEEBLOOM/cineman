package com.codebloom.cineman.repository;

import com.codebloom.cineman.model.MovieEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MovieRepository extends JpaRepository<MovieEntity,Integer> {
    List<MovieEntity> findByTitleContainingIgnoreCase(String title);
}
