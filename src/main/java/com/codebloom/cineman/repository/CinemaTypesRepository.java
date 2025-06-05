package com.codebloom.cineman.repository;

import com.codebloom.cineman.model.CinemaTypesEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CinemaTypesRepository extends JpaRepository<CinemaTypesEntity,Integer> {
}
