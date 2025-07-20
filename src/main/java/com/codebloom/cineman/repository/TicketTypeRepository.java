package com.codebloom.cineman.repository;

import com.codebloom.cineman.common.enums.TicketType;
import com.codebloom.cineman.model.TicketTypeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface TicketTypeRepository extends JpaRepository<TicketTypeEntity,Integer> {
    Optional<TicketTypeEntity> findByNameAndStatus(TicketType type, Boolean status);
}
