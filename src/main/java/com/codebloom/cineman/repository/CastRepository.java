package com.codebloom.cineman.repository;

import com.codebloom.cineman.model.CastEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CastRepository extends JpaRepository <CastEntity,Integer > {
}
