package com.codebloom.cineman.repository;

import com.codebloom.cineman.model.MovieEntity;
import com.codebloom.cineman.model.MovieStatusEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MovieRepository extends JpaRepository<MovieEntity,Integer> {
    List<MovieEntity> findByTitleContainingIgnoreCase(String title);

    Page<MovieEntity> findAllByStatus(MovieStatusEntity status, Pageable pageable);
}
