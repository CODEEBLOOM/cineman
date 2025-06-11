package com.codebloom.cineman.repository;

import com.codebloom.cineman.model.SnackEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SnackRepository extends JpaRepository<SnackEntity,Integer> {
}
