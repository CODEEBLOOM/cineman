package com.codebloom.cineman.repository;

import com.codebloom.cineman.common.enums.TicketType;
import com.codebloom.cineman.model.TicketTypeEntity;
import com.codebloom.cineman.repository.projection.TicketTypeSelectionProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface TicketTypeRepository extends JpaRepository<TicketTypeEntity,Integer> {
    List<TicketTypeEntity> findAllByStatus(Boolean status);
    Optional<TicketTypeEntity> findByIdAndStatus(Integer id, Boolean status);
    Optional<TicketTypeEntity> findByName(TicketType type);
    Optional<TicketTypeEntity> findByNameAndStatus(TicketType type, Boolean status);
    Optional<TicketTypeEntity> findByNameAndStatusAndIdNot(TicketType type, Boolean status, Integer id);

    @Query("""
            SELECT tt.id AS id, tt.price AS price
            FROM TicketTypeEntity tt
            WHERE tt.name = :type AND tt.status = :status
            """)
    Optional<TicketTypeSelectionProjection> findSelectionByNameAndStatus(TicketType type, Boolean status);

}
