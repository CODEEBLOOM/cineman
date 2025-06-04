package com.codebloom.cineman.repository;

import com.codebloom.cineman.model.TicketTypeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TicketTypeRepository extends JpaRepository<TicketTypeEntity,Integer> {
}
