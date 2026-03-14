package com.codebloom.cineman.repository;

import com.codebloom.cineman.model.MovieVariationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MovieVariationRepository extends JpaRepository<MovieVariationEntity,Integer> {
    List<MovieVariationEntity> findAllByStatus(Boolean status);

    Optional<MovieVariationEntity> findByIdAndStatus(Integer id, Boolean status);

    Optional<MovieVariationEntity> findByNameAndStatus(String name, Boolean status);

    Optional<MovieVariationEntity> findByNameAndStatusAndIdNot(String name, Boolean status, Integer id);
}
