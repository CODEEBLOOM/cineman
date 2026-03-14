package com.codebloom.cineman.repository;

import com.codebloom.cineman.model.MovieVariationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MovieVariationRepository extends JpaRepository<MovieVariationEntity,Integer> {
}
