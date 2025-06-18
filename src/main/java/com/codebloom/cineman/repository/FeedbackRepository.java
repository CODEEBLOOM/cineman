package com.codebloom.cineman.repository;

import com.codebloom.cineman.common.enums.SatisfactionLevel;
import com.codebloom.cineman.model.FeedbackEntity;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface FeedbackRepository extends JpaRepository<FeedbackEntity,Integer> {
	List<FeedbackEntity> findByUser_Email(String email);
	List<FeedbackEntity> findBySatisfactionLevel(SatisfactionLevel level);
}
