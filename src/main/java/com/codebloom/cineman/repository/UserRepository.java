package com.codebloom.cineman.repository;


import com.codebloom.cineman.common.enums.UserStatus;
import com.codebloom.cineman.model.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {

    Optional<UserEntity> findByEmail(String email);
    Optional<UserEntity> findByEmailAndStatus(String email, UserStatus status);

    Optional<UserEntity> findByPhoneNumber(String phoneNumber);

    UserEntity findByRefreshToken(String refreshToken);

    Optional<UserEntity> findByEmailAndPhoneNumberAndUserIdNot(String email, String phoneNumber, Long userId);
}
