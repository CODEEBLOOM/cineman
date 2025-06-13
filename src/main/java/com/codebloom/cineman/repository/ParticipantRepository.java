package com.codebloom.cineman.repository;

import com.codebloom.cineman.model.ParticipantEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ParticipantRepository extends JpaRepository<ParticipantEntity,Integer> {
    
}
