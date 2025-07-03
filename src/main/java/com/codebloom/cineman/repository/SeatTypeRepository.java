package com.codebloom.cineman.repository;

import com.codebloom.cineman.model.SeatTypeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SeatTypeRepository extends JpaRepository<SeatTypeEntity, String> {
    Optional<SeatTypeEntity> findByName(String name);

    Optional<SeatTypeEntity> findByNameAndId(String name, String seatTypeId);

    Optional<SeatTypeEntity> findByStatusAndName(Boolean status, String name);

    Optional<SeatTypeEntity> findByStatusAndId(Boolean status, String seatTypeId);

    Optional<SeatTypeEntity> findByNameAndStatusAndIdNot(String name, boolean status, String id);

    List<SeatTypeEntity> findAllByStatus(Boolean status);

    Optional<SeatTypeEntity> findByNameAndStatus(String name, Boolean status);
}
