package com.codebloom.cineman.repository;

import com.codebloom.cineman.common.enums.SeatStatus;
import com.codebloom.cineman.model.CinemaTheaterEntity;
import com.codebloom.cineman.model.SeatEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SeatRepository extends JpaRepository<SeatEntity,Long> {
    Optional<SeatEntity> findByIdAndStatusNot(Long id, SeatStatus status);

    Optional<SeatEntity> findByIdAndStatus(Long id, SeatStatus status);

    Optional<SeatEntity> findByIdAndCinemaTheaterAndStatusNot(Long id, CinemaTheaterEntity cinemaTheater, SeatStatus status);

    List<SeatEntity> findAllByStatusNotAndCinemaTheater(SeatStatus status, CinemaTheaterEntity cinemaTheater);
}
