package com.codebloom.cineman.service;


import com.codebloom.cineman.controller.request.*;
import com.codebloom.cineman.controller.response.MovieResponse;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;


public interface MovieService  {

    Page<MovieResponse> findAllByPage(PageQueryRequest request);
    List<MovieResponse> findAll();
    MovieResponse findById(Integer id);

    Integer save(MovieCreationRequest movie);
    MovieResponse update(Integer movieId,MovieUpdateRequest movie);
    void delete(Integer id );
    List<MovieResponse> findByTitle(String title);
}
