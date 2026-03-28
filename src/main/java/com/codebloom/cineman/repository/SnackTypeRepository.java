package com.codebloom.cineman.repository;

import com.codebloom.cineman.model.SnackTypeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SnackTypeRepository extends JpaRepository<SnackTypeEntity,Integer> {

    List<SnackTypeEntity> findByIsActive(Boolean isActive);

    Optional<SnackTypeEntity> findByIdAndIsActive(Integer id, Boolean isActive);

    Optional<SnackTypeEntity> findByNameAndIsActive(String combo, Boolean active);
}
