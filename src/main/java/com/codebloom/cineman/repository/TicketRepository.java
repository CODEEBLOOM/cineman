package com.codebloom.cineman.repository;

import com.codebloom.cineman.controller.response.RatingResponse;
import com.codebloom.cineman.model.TicketEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;

public interface TicketRepository extends JpaRepository<TicketEntity, Long> {

	@Query("SELECT AVG(t.rating) FROM TicketEntity t WHERE t.showTime.movie.movieId = :movieId AND t.rating IS NOT NULL")
	Double findAverageRatingByMovieId(@Param("movieId") Integer movieId);
	
    @Query("SELECT t FROM TicketEntity t WHERE t.invoice.customer.userId = :userId")
    List<TicketEntity> findAllByUserId(Long userId);

    @Query("SELECT t FROM TicketEntity t WHERE t.showTime.movie.movieId = :movieId")
    List<TicketEntity> findAllByMovieId(Integer movieId);


}
