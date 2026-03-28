package com.codebloom.cineman.repository;

import com.codebloom.cineman.model.MembershipRankEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MembershipRankRepository extends JpaRepository<MembershipRankEntity, Integer> {
    Optional<MembershipRankEntity> findByName(String name);

    Optional<MembershipRankEntity> findByNameAndStatus(String name, Boolean status);

    Optional<MembershipRankEntity> findByIdAndStatus(Integer id, Boolean status);

    List<MembershipRankEntity> findAllByStatusOrderByPriorityLevelAsc(Boolean status);
}
