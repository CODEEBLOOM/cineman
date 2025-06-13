package com.codebloom.cineman.repository;

import com.codebloom.cineman.model.ParticipantEntity;
import com.codebloom.cineman.model.MovieParticipantEntity;
import com.codebloom.cineman.model.MovieEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MovieParticipantRepository extends JpaRepository<MovieParticipantEntity, Integer> {
    @Query("SELECT COUNT(mp) > 0 FROM MovieParticipantEntity mp WHERE mp.participant.participantId = :participantId")
    boolean existsByParticipantId(@Param("participantId") Integer  participantId);

    @Query("SELECT COUNT(mp) > 0 FROM MovieParticipantEntity mp WHERE mp.movieRole.movieRoleId = :movieRoleId")
    boolean existsByMovieRoleId(@Param("movieRoleId") Integer  movieRoleId);

    Optional<MovieParticipantEntity> findByMovieAndParticipant(MovieEntity existingMovie, ParticipantEntity exitingParticipant);
}
