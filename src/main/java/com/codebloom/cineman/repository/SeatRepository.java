package com.codebloom.cineman.repository;

import com.codebloom.cineman.common.enums.SeatStatus;
import com.codebloom.cineman.model.CinemaTheaterEntity;
import com.codebloom.cineman.model.SeatEntity;
import com.codebloom.cineman.model.SeatMapResponse;
import com.codebloom.cineman.repository.projection.SeatSelectionProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SeatRepository extends JpaRepository<SeatEntity,Long> {
    Optional<SeatEntity> findByIdAndStatusNot(Long id, SeatStatus status);

    Optional<SeatEntity> findByIdAndStatus(Long id, SeatStatus status);

    Optional<SeatEntity> findByIdAndCinemaTheaterAndStatusNot(Long id, CinemaTheaterEntity cinemaTheater, SeatStatus status);

    List<SeatEntity> findAllByStatusNotAndCinemaTheater(SeatStatus status, CinemaTheaterEntity cinemaTheater);

    @Query("""
            SELECT s.id AS id,
                   s.rowIndex AS rowIndex,
                   s.columnIndex AS columnIndex,
                   s.label AS label,
                   s.status AS status,
                   st.id AS seatTypeId,
                   st.name AS seatTypeName,
                   st.price AS seatTypePrice,
                   st.status AS seatTypeStatus
            FROM SeatEntity s
            JOIN s.seatType st
            WHERE s.id = :id AND s.status = :status
            """)
    Optional<SeatSelectionProjection> findSeatSelectionByIdAndStatus(Long id, SeatStatus status);

}
