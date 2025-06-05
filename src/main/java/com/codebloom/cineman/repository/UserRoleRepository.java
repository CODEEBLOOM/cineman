package com.codebloom.cineman.repository;

import com.codebloom.cineman.model.Id.UserRoleId;
import com.codebloom.cineman.model.UserRoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRoleRepository extends JpaRepository<UserRoleEntity, UserRoleId> {
}
