package com.codebloom.cineman.repository;

import com.codebloom.cineman.model.RefreshTokenEntity;
import com.codebloom.cineman.model.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TokenRepositoty extends JpaRepository<RefreshTokenEntity, Long> {
    Optional<RefreshTokenEntity> findByUser(UserEntity user);
    Optional<RefreshTokenEntity> findByToken(String token);
}
