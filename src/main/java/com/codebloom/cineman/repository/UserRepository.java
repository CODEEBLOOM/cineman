package com.codebloom.cineman.repository;


import com.codebloom.cineman.common.enums.SatisfactionLevel;
import com.codebloom.cineman.common.enums.UserStatus;
import com.codebloom.cineman.common.enums.UserType;
import com.codebloom.cineman.model.FeedbackEntity;
import com.codebloom.cineman.model.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {

    Optional<UserEntity> findByEmail(String email);

    Optional<UserEntity> findByPhoneNumber(String phoneNumber);

    UserEntity findByRefreshToken(String refreshToken);

	List<UserEntity> findByUserType(UserType admin); // thang - feedback gửi mail cho admin

    Optional<UserEntity> findByEmailAndPhoneNumberAndUserIdNot(String email, String phoneNumber, Long userId);

    Optional<UserEntity> findByEmailAndStatus(String email, UserStatus status);

}
