package com.codebloom.cineman.repository;

import com.codebloom.cineman.common.enums.SeatType;
import com.codebloom.cineman.model.SeatTypeEntity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SeatTypeRepository extends JpaRepository<SeatTypeEntity, SeatType> {
    Optional<SeatTypeEntity> findByName(String name);

    Optional<SeatTypeEntity> findByIdAndStatus(SeatType id,Boolean status);

    Optional<SeatTypeEntity> findByNameAndStatusAndIdNot(String name, boolean status, SeatType id);

    List<SeatTypeEntity> findAllByStatus(Boolean status);

    Optional<SeatTypeEntity> findByNameAndStatus(String name, Boolean status);

    Optional<SeatTypeEntity> findByIdAndStatus(SeatType id, boolean b);
}
