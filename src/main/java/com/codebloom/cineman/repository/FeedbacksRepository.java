package com.codebloom.cineman.repository;

import com.codebloom.cineman.model.FeedbacksEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FeedbacksRepository extends JpaRepository<FeedbacksEntity,Integer> {
}
