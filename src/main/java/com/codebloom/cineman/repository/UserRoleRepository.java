package com.codebloom.cineman.repository;

import com.codebloom.cineman.model.UserRoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRoleRepository extends JpaRepository<UserRoleEntity, Long > {
    boolean existsByRole_RoleId(String roleId);
    List<UserRoleEntity> findAllByUser_UserId(Long userId);
}
