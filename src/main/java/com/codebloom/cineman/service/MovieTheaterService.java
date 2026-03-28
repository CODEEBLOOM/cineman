package com.codebloom.cineman.service;


import com.codebloom.cineman.controller.request.MovieTheaterRequest;
import com.codebloom.cineman.controller.request.PageRequest;
import com.codebloom.cineman.controller.response.MovieTheaterPage;
import com.codebloom.cineman.controller.response.MovieTheaterResponse;

import java.util.List;



public interface MovieTheaterService {

    MovieTheaterPage findAllByPage(PageRequest pageRequest);
    List<MovieTheaterResponse> findAllByProvinceId(Integer provinceId);
    MovieTheaterResponse findById(Integer id);
    MovieTheaterResponse save(MovieTheaterRequest movie);
    MovieTheaterResponse update(Integer id, MovieTheaterRequest movie);
    void delete(Integer id);


}
