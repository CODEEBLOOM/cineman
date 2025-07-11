package com.codebloom.cineman.repository;


import com.codebloom.cineman.common.enums.ShowTimeStatus;
import com.codebloom.cineman.model.CinemaTheaterEntity;
import com.codebloom.cineman.model.MovieEntity;
import com.codebloom.cineman.model.ShowTimeEntity;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface ShowTimeRepository extends JpaRepository<ShowTimeEntity, Long> {

    List<ShowTimeEntity> findAllByCinemaTheaterAndShowDateAndStatusNot(CinemaTheaterEntity cinemaTheater, Date showDate, ShowTimeStatus showTimeStatus, Sort sort);


    List<ShowTimeEntity> findAllByStatusNot(ShowTimeStatus showTimeStatus);

    Optional<ShowTimeEntity> findByIdAndStatusNot(Long id, ShowTimeStatus status);

    List<ShowTimeEntity> findAllByMovieAndStatusNot(MovieEntity movie, ShowTimeStatus showTimeStatus);

    List<ShowTimeEntity> findAllByCinemaTheaterAndStatusNot(CinemaTheaterEntity cinemaTheater, ShowTimeStatus showTimeStatus, Sort sort);

    @Query("""
            SELECT st
            FROM ShowTimeEntity st
            WHERE st.movie.movieId = :movieId
            AND st.showDate >= CURRENT_DATE
            AND st.cinemaTheater.movieTheater.movieTheaterId = :movieTheaterId""")
    List<ShowTimeEntity> findAllShowTimeByMovieIdAndMovieTheaterId(Integer movieId, ShowTimeStatus showTimeStatus, Integer movieTheaterId, Sort sort);

    ;

    @Query("""
            SELECT st
            FROM ShowTimeEntity st
            WHERE st.movie.movieId = :movieId
            AND st.showDate >= :showDate
            AND st.cinemaTheater.movieTheater.movieTheaterId = :cinemaTheaterId
            AND st.status = :showTimeStatus
            """)
    List<ShowTimeEntity> findAllShowTimeByMovieIdAndMovieTheaterIdAndShowDateEqual(Integer movieId, Date showDate, Integer cinemaTheaterId, ShowTimeStatus showTimeStatus, Sort sort);

    @Query("""
            SELECT COUNT(s.id)
            FROM ShowTimeEntity st JOIN CinemaTheaterEntity c ON c.cinemaTheaterId = st.cinemaTheater.cinemaTheaterId
                        JOIN SeatEntity s ON s.cinemaTheater.cinemaTheaterId = c.cinemaTheaterId
            WHERE st.id = :showTimeId
                        AND st.status = :showTimeStatus
                        AND s.id NOT IN (SELECT seat.id FROM TicketEntity t JOIN SeatEntity seat ON seat.id = t.seat.id WHERE t.showTime.id = :showTimeId)
            """)
    Long countSeatByShowTimeId(@Param("showTimeId")Long showTimeId, @Param("showTimeStatus")ShowTimeStatus showTimeStatus);

}
