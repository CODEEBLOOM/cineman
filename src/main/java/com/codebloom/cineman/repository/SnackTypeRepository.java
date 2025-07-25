package com.codebloom.cineman.repository;

import com.codebloom.cineman.model.SnackTypeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SnackTypeRepository extends JpaRepository<SnackTypeEntity,Integer> {

    SnackTypeEntity findByNameAndIsActive(String combo, Boolean active);
}
