package com.codebloom.cineman.repository;

import com.codebloom.cineman.model.SeatEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SeatRepository extends JpaRepository<SeatEntity,Long> {
}
