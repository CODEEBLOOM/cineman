package com.codebloom.cineman.repository;

import com.codebloom.cineman.model.MembershipRankEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MembershipRankRepository extends JpaRepository<MembershipRankEntity, Long> {
    Optional<MembershipRankEntity> findByName(String name);
}
