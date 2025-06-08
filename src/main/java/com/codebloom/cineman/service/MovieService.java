package com.codebloom.cineman.service;


import com.codebloom.cineman.controller.request.*;
import com.codebloom.cineman.controller.response.MovieResponse;
import com.codebloom.cineman.controller.response.MovieResponse;
import com.codebloom.cineman.model.MovieEntity;
import com.codebloom.cineman.repository.MovieRepository;
import com.codebloom.cineman.service.impl.CRUDService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface MovieService  {

    Page<MovieResponse> findAllByPage(PageQueryRequest request);
    List<MovieResponse> findAll();
    MovieResponse findById(Integer id);

    Integer save(MovieCreationRequest movie);
    MovieResponse update(Integer movieId,MovieUpdateRequest movie);
    void delete(Integer id );

}
