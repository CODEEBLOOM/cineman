package com.codebloom.cineman.repository;

import com.codebloom.cineman.model.PromotionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PromotionRepository extends JpaRepository<PromotionEntity,Long> {

}
