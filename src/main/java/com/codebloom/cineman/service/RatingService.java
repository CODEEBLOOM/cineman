package com.codebloom.cineman.service;

import java.util.List;

import com.codebloom.cineman.controller.request.RatingRequest;
import com.codebloom.cineman.controller.response.RatingResponse;

public interface RatingService {

	 RatingResponse saveRating(Long userId, RatingRequest request);
	 
	 RatingResponse getRatingByTicket(Long userId, Long ticketId);
	 
	 List<RatingResponse> getRatingsByEmail(String email);

	 List<RatingResponse> getRatingsByUser(Long userId);
	 
	 List<RatingResponse> getRatingsByMovie(Integer movieId);
	 
	 List<RatingResponse> getRatingsByUserAndMovie(Long userId, Integer movieId);
	 
//	 void deleteRating(Long userId, Long ticketId);
}
