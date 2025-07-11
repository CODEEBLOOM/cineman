package com.codebloom.cineman.repository;

import com.codebloom.cineman.common.enums.CinemaTheaterStatus;
import com.codebloom.cineman.model.CinemaTheaterEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


import java.util.Optional;

@Repository
public interface CinemaTheatersRepository extends JpaRepository<CinemaTheaterEntity,Integer > {
    Optional<CinemaTheaterEntity> findByStatusNotAndCinemaTheaterId(CinemaTheaterStatus status, Integer cinemaTheaterId);

    Page<CinemaTheaterEntity> findAllByStatusNot(CinemaTheaterStatus status, Pageable pageable);

    Page<CinemaTheaterEntity> findAllByStatus(CinemaTheaterStatus cinemaTheaterStatus, Pageable pageable);

    Optional<CinemaTheaterEntity> findByStatusAndCinemaTheaterId(CinemaTheaterStatus  status, Integer cinemaTheaterId);
}
