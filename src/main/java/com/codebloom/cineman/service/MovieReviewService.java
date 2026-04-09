package com.codebloom.cineman.service;

import com.codebloom.cineman.controller.request.MovieReviewRequest;
import com.codebloom.cineman.controller.response.MovieReviewContextResponse;
import com.codebloom.cineman.controller.response.MovieReviewPageResponse;
import com.codebloom.cineman.controller.response.MovieReviewResponse;

public interface MovieReviewService {

    MovieReviewPageResponse findAllByMovieId(Integer movieId, int page, int size);

    MovieReviewContextResponse getReviewContext(Integer movieId);

    MovieReviewResponse create(Integer movieId, MovieReviewRequest request);

    MovieReviewResponse update(Integer movieId, MovieReviewRequest request);

    void delete(Integer movieId);
}
