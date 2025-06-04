package com.codebloom.cineman.repository;

import com.codebloom.cineman.model.MovieStatusEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MovieStatusRepository extends JpaRepository<MovieStatusEntity,String> {
}
