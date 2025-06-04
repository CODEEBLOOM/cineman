package com.codebloom.cineman.repository;

import com.codebloom.cineman.model.SnackTypeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SnackTypeRepository extends JpaRepository<SnackTypeEntity,Integer> {
    // Additional query methods can be defined here if needed

}
