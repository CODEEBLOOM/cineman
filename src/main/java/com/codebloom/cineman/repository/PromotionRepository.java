package com.codebloom.cineman.repository;

import com.codebloom.cineman.common.enums.StatusPromotion;
import com.codebloom.cineman.model.PromotionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PromotionRepository extends JpaRepository<PromotionEntity,Long> {

    Optional<PromotionEntity> findByCodeAndStatus(String code, StatusPromotion statusPromotion);

    List<PromotionEntity> findAllByStatus(StatusPromotion status);
}
