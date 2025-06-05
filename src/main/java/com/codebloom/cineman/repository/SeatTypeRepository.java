package com.codebloom.cineman.repository;

import com.codebloom.cineman.model.SeatTypeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SeatTypeRepository extends JpaRepository<SeatTypeEntity,Integer> {
}
