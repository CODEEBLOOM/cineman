package com.codebloom.cineman.repository;

import com.codebloom.cineman.model.GenresEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GenresRepository extends JpaRepository<GenresEntity,Integer> {
}
