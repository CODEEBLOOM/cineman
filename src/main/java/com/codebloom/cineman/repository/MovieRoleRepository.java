package com.codebloom.cineman.repository;

import com.codebloom.cineman.model.MovieRoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MovieRoleRepository extends JpaRepository<MovieRoleEntity, Integer> {
    Optional<MovieRoleEntity> findByName(String trim);

    Optional<MovieRoleEntity> findByMovieRoleIdAndActive(Integer id, boolean active);

    List<MovieRoleEntity> findAllByActive(boolean active);

    Optional<MovieRoleEntity> findByNameAndActive(String name, boolean active);
}
