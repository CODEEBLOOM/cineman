package com.codebloom.cineman.repository;

import com.codebloom.cineman.model.SeatTypeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SeatTypeRepository extends JpaRepository<SeatTypeEntity,Integer> {
}
