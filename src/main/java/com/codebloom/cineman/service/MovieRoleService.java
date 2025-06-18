package com.codebloom.cineman.service;

import com.codebloom.cineman.controller.request.MovieRoleRequest;
import com.codebloom.cineman.model.MovieRoleEntity;

import java.util.List;

public interface MovieRoleService {

    List<MovieRoleEntity> findAll();
    MovieRoleEntity findById(Integer id);
    MovieRoleEntity create(MovieRoleRequest movieRoleRequest);
    MovieRoleEntity update(Integer id, MovieRoleRequest movieRoleRequest);
    void delete(Integer movieRoleId);

}
