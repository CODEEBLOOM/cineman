package com.codebloom.cineman.repository;

import com.codebloom.cineman.model.FeedbackTopicEntity;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface FeedbackTopicRepository extends JpaRepository<FeedbackTopicEntity,Integer> {
	Optional<FeedbackTopicEntity> findByTopicName(String topicName);
	List<FeedbackTopicEntity> findByIsActiveTrue();
	Optional<FeedbackTopicEntity> findByTopicIdAndIsActiveTrue(Integer topicId);

	@Query("SELECT COUNT(f) > 0 FROM FeedbackTopicEntity f " +
	           "WHERE LOWER(f.topicName) = LOWER(:topicName) " +
	           "AND f.topicId <> :id " +
	           "AND f.isActive = true")
	boolean isTopicNameDuplicated(@Param("topicName") String topicName, @Param("id") Integer id);
}
