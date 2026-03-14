package com.codebloom.cineman.repository;

import com.codebloom.cineman.common.enums.ShowTimeStatus;
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

    Page<MovieEntity> findAllByReleaseDateGreaterThanEqualAndStatus(Date targetDate, MovieStatusEntity status, Pageable pageable);

    @Query(""" 
            SELECT DISTINCT st.movie
            FROM ShowTimeEntity st
            WHERE st.cinemaTheater.movieTheater.movieTheaterId = :movieTheaterId
              AND st.showDate >= CURRENT_DATE
            """)
    Page<MovieEntity> findAllByStatusAndMovieTheaterId(MovieStatusEntity status, Integer movieTheaterId, Pageable pageable);

    @Query("""
            SELECT DISTINCT st.movie
            FROM ShowTimeEntity st
            WHERE st.cinemaTheater.cinemaTheaterId = :cinemaTheaterId
                        AND st.status = :showTimeStatus
                        AND st.showDate >= :showDate
            """)
    Page<MovieEntity> findAllMovieByCinemaTheaterIdAndShowDate(Integer cinemaTheaterId, ShowTimeStatus showTimeStatus, Date showDate, Pageable pageable);

    Page<MovieEntity> findAllByStatusNot(MovieStatusEntity status, Pageable pageable);
}
