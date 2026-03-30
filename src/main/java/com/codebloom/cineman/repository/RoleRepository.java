package com.codebloom.cineman.repository;

import com.codebloom.cineman.model.RoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<RoleEntity,String> {
    Optional<RoleEntity> findByName(String name);
    Optional<RoleEntity> findByNameIgnoreCase(String name);
    List<RoleEntity> findAllByOrderByRoleIdAsc();
}
