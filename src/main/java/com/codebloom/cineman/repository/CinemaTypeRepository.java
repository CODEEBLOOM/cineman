package com.codebloom.cineman.repository;

import com.codebloom.cineman.model.CinemaTypeEntity;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


import java.util.List;
import java.util.Optional;

@Repository
public interface CinemaTypeRepository extends JpaRepository<CinemaTypeEntity,Integer> {
   Optional<CinemaTypeEntity> findByCode(String code);

    Optional<CinemaTypeEntity> findByCodeAndCinemaTypeIdNot(String code, Integer cinemaTypeId);

    Optional<CinemaTypeEntity> findByStatusAndCinemaTypeId(Boolean status, Integer cinemaTypeId);

    Optional<CinemaTypeEntity> findByCinemaTypeIdAndStatus(Integer cinemaTypeId, Boolean status);

    List<CinemaTypeEntity> findAllByStatus(boolean status);

    Optional<CinemaTypeEntity> findByCodeAndStatusAndCinemaTypeIdNot( String code, Boolean status, Integer id);

    Optional<CinemaTypeEntity> findByCodeAndStatus(String code, Boolean status);
}
