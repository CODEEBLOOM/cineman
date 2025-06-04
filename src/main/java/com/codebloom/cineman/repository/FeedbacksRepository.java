package com.codebloom.cineman.repository;

import com.codebloom.cineman.model.FeedbacksEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FeedbacksRepository extends JpaRepository<FeedbacksEntity,Integer> {
}
