package com.codebloom.cineman.repository;

import com.codebloom.cineman.model.MovieReviewEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MovieReviewRepository extends JpaRepository<MovieReviewEntity, Long> {

    Page<MovieReviewEntity> findAllByMovieMovieId(Integer movieId, Pageable pageable);

    Optional<MovieReviewEntity> findByMovieMovieIdAndUserUserId(Integer movieId, Long userId);

    boolean existsByMovieMovieIdAndUserUserId(Integer movieId, Long userId);

    long countByMovieMovieId(Integer movieId);

    @Query("""
            SELECT AVG(mr.ratingScore)
            FROM MovieReviewEntity mr
            WHERE mr.movie.movieId = :movieId
            """)
    Double findAverageRatingByMovieId(Integer movieId);
}
