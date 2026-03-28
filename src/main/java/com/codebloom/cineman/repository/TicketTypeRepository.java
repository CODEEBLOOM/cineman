package com.codebloom.cineman.repository;

import com.codebloom.cineman.common.enums.TicketType;
import com.codebloom.cineman.model.TicketTypeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface TicketTypeRepository extends JpaRepository<TicketTypeEntity,Integer> {
    List<TicketTypeEntity> findAllByStatus(Boolean status);
    Optional<TicketTypeEntity> findByIdAndStatus(Integer id, Boolean status);
    Optional<TicketTypeEntity> findByName(TicketType type);
    Optional<TicketTypeEntity> findByNameAndStatus(TicketType type, Boolean status);
}
