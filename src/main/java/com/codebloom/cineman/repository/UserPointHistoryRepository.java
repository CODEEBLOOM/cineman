package com.codebloom.cineman.repository;

import com.codebloom.cineman.controller.request.UserPointHistoryRequest;
import com.codebloom.cineman.model.UserPointHistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserPointHistoryRepository extends JpaRepository<UserPointHistoryEntity, Integer> {
}
