package com.codebloom.cineman.repository;

import com.codebloom.cineman.model.TicketEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TicketRepository extends JpaRepository<TicketEntity,Long> {
}
