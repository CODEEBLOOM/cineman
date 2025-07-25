package com.codebloom.cineman.repository;

import com.codebloom.cineman.model.SnackEntity;
import com.codebloom.cineman.model.SnackTypeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SnackRepository extends JpaRepository<SnackEntity,Integer> {
    List<SnackEntity> findByIsActive(Boolean isActive);

    Optional<SnackEntity> findByIdAndIsActive(Integer id, Boolean isActive);

    List<SnackEntity> findBySnackTypeAndIsActive(SnackTypeEntity snackType, Boolean isActive);
}
