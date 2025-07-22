package com.codebloom.cineman.repository;

import com.codebloom.cineman.common.enums.StatusPromotion;
import com.codebloom.cineman.model.PromotionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface PromotionRepository extends JpaRepository<PromotionEntity,Long> {
    Optional<PromotionEntity> findByCode(String code);

    List<PromotionEntity> findByStatusAndStartDayBeforeAndEndDayAfter(StatusPromotion status, Date start, Date end);

    boolean existsByCode(String code);
}
