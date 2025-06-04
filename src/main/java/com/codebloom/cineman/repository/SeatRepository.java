package com.codebloom.cineman.repository;

import com.codebloom.cineman.model.SeatEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SeatRepository extends JpaRepository<SeatEntity,Long> {
}
