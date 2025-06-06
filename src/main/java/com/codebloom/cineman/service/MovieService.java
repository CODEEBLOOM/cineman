package com.codebloom.cineman.service;


import com.codebloom.cineman.controller.request.MovieCreationRequest;
import com.codebloom.cineman.controller.request.MovieUpdateRequest;
import com.codebloom.cineman.controller.request.UserCreationRequest;
import com.codebloom.cineman.controller.request.UserUpdateRequest;
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

    Page<MovieEntity> findAllByPage(int page, int size, String sortBy, String sortDir, String searchTerm);
    List<MovieEntity> findAll();
    MovieResponse findById(Integer id);

    Integer save(MovieCreationRequest movie);
    void update(MovieUpdateRequest movie);
    void delete(Integer id );

}
