package com.codebloom.cineman.repository;

import com.codebloom.cineman.model.CinemaTypeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CinemaTypeRepository extends JpaRepository<CinemaTypeEntity,Integer> {
   Optional<CinemaTypeEntity> findByCode(String code);

    Optional<CinemaTypeEntity> findByCodeAndCinemaTypeIdNot(String code, Integer cinemaTypeId);
}
