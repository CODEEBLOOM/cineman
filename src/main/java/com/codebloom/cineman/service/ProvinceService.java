package com.codebloom.cineman.service;

import com.codebloom.cineman.controller.request.ProvinceRequest;
import com.codebloom.cineman.model.ProvinceEntity;

import java.util.List;

public interface ProvinceService {

    List<ProvinceEntity> findAll();

    ProvinceEntity findById(Integer id);

    ProvinceEntity findByName(String provinceName);

    ProvinceEntity save(ProvinceRequest province);

    ProvinceEntity update(Integer id, ProvinceRequest province);

    void delete(Integer id);

    void deleteByCode(Integer provinceCode);

}
