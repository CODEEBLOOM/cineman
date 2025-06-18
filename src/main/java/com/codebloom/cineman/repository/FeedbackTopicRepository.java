package com.codebloom.cineman.repository;

import com.codebloom.cineman.model.FeedbackTopicEntity;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FeedbackTopicRepository extends JpaRepository<FeedbackTopicEntity,Integer> {

	Optional<FeedbackTopicEntity> findByTopicName(String topicName);
}
