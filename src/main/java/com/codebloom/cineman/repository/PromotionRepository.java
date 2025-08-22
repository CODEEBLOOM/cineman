package com.codebloom.cineman.repository;

import com.codebloom.cineman.common.enums.StatusPromotion;
import com.codebloom.cineman.model.PromotionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PromotionRepository extends JpaRepository<PromotionEntity, Long> {

    Optional<PromotionEntity> findByCodeAndStatus(String code, StatusPromotion statusPromotion);

    List<PromotionEntity> findAllByStatus(StatusPromotion status);

    @Query(value = """
            SELECT p FROM PromotionEntity p WHERE p.status = :status AND p.id NOT IN (
                        SELECT DISTINCT p.id FROM InvoiceEntity i
                        JOIN i.promotion p
                        WHERE i.customer.userId = :userId AND i.promotion IS NOT NULL
                        )""")
    List<PromotionEntity> findAllPromotionByCustomerNotUse(StatusPromotion status, Long userId);

    @Query(value = """
            SELECT p FROM PromotionEntity p WHERE p.status = :status AND p.id IN (
                        SELECT DISTINCT p.id FROM InvoiceEntity i
                        JOIN i.promotion p
                        WHERE i.customer.userId = :userId AND i.promotion IS NOT NULL
                        )""")
    List<PromotionEntity> findAllPromotionByCustomerUsed(StatusPromotion status, Long userId);
}
