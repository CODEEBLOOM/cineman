package com.codebloom.cineman.repository;

import com.codebloom.cineman.model.CastEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CastRepository extends JpaRepository <CastEntity,Integer > {
}
