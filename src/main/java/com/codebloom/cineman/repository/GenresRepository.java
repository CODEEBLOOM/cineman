package com.codebloom.cineman.repository;

import com.codebloom.cineman.model.GenresEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GenresRepository extends JpaRepository<GenresEntity,Integer> {
}
