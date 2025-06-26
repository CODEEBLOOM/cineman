package com.codebloom.cineman.repository;

import com.codebloom.cineman.common.enums.SeatMapStatus;
import com.codebloom.cineman.model.SeatMapEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SeatMapRepository extends JpaRepository<SeatMapEntity, Integer> {
    List<SeatMapEntity> findByStatus(SeatMapStatus status);

    List<SeatMapEntity> findAllByStatusNot(SeatMapStatus status);
    Page<SeatMapEntity> findAllByStatusNot(SeatMapStatus status, Pageable pageable);

    Optional<SeatMapEntity> findByIdAndStatusNot(Integer id, SeatMapStatus status);

    Page<SeatMapEntity> findAllByStatus(SeatMapStatus seatMapStatus, PageRequest pageReq);
}
