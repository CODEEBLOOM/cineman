package com.codebloom.cineman.repository;

import com.codebloom.cineman.model.DirectorEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DirectorRepository extends JpaRepository<DirectorEntity,Integer> {
}
