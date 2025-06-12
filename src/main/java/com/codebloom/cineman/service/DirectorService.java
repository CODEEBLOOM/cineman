package com.codebloom.cineman.service;

import com.codebloom.cineman.controller.response.DirectorRequest;
import com.codebloom.cineman.model.DirectorEntity;

import java.util.List;

public interface DirectorService {

    List<DirectorEntity> findAll();
    DirectorEntity findById(Integer id);
    DirectorEntity save(DirectorRequest director);
    DirectorEntity update(Integer id, DirectorRequest director);
    int delete(Integer id);

}
