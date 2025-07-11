package com.codebloom.cineman.repository;

import com.codebloom.cineman.model.MovieEntity;
import com.codebloom.cineman.model.MovieStatusEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;

@Repository
public interface MovieRepository extends JpaRepository<MovieEntity, Integer> {
    Page<MovieEntity> findAllByStatus(MovieStatusEntity status, Pageable pageable);

    Page<MovieEntity> findAllByReleaseDateGreaterThanAndStatus(Date targetDate, MovieStatusEntity status, Pageable pageable);

    @Query(""" 
            SELECT DISTINCT st.movie
            FROM ShowTimeEntity st
            WHERE st.cinemaTheater.movieTheater.movieTheaterId = :movieTheaterId
              AND st.showDate >= CURRENT_DATE
            """)
    Page<MovieEntity> findAllByStatusAndMovieTheaterId(MovieStatusEntity status, Integer movieTheaterId, Pageable pageable);
}
