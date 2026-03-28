package com.codebloom.cineman.repository;

import com.codebloom.cineman.common.enums.TicketType;
import com.codebloom.cineman.model.TicketTypeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;


@Repository
public interface TicketTypeRepository extends JpaRepository<TicketTypeEntity,Integer> {
    Optional<TicketTypeEntity> findByNameAndStatus(TicketType type, Boolean status);

    Optional<TicketTypeEntity> findByIdAndStatus(Integer id, Boolean status);

    Optional<TicketTypeEntity> findByNameAndStatusAndIdNot(TicketType type, Boolean status, Integer id);

    List<TicketTypeEntity> findAllByStatus(Boolean status);
}
