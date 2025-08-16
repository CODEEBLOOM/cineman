package com.codebloom.cineman.repository;


import com.codebloom.cineman.model.UserEntity;
import com.codebloom.cineman.model.UserPointHistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserPointHistoryRepository extends JpaRepository<UserPointHistoryEntity, Integer> {
    List<UserPointHistoryEntity> findAllByUser(UserEntity user);
}
