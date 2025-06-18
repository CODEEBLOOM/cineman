package com.codebloom.cineman.repository;

import com.codebloom.cineman.model.PermissionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PermissionRepository extends JpaRepository<PermissionEntity,Integer> {
    @Query("""
        SELECT p FROM PermissionEntity p
        JOIN p.roles r
        JOIN r.userRoles ur
        WHERE ur.user.userId = :userId
    """)
    List<PermissionEntity> findAllByUserId(@Param("userId") Long userId);
}


