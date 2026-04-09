package com.codebloom.cineman.repository;

import com.codebloom.cineman.model.PromotionTypeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PromotionTypeRepository extends JpaRepository<PromotionTypeEntity, Long> {

    Optional<PromotionTypeEntity> findByCode(String code);

    Optional<PromotionTypeEntity> findByCodeAndStatus(String code, Boolean status);

    Optional<PromotionTypeEntity> findByName(String name);

    Optional<PromotionTypeEntity> findByIdAndStatus(Long id, Boolean status);

    List<PromotionTypeEntity> findAllByStatus(Boolean status);
}
