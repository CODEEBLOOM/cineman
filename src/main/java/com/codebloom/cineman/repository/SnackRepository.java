package com.codebloom.cineman.repository;

import com.codebloom.cineman.model.SnackEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SnackRepository extends JpaRepository<SnackEntity,Integer> {
}
