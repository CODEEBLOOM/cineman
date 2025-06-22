package com.codebloom.cineman.repository;

import com.codebloom.cineman.model.ProvinceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProvinceRepository extends JpaRepository<ProvinceEntity, Integer> {
    Optional<ProvinceEntity> findByName(String name);

    Optional<ProvinceEntity> findByCode(Integer code);

    Optional<ProvinceEntity> findByNameAndCodeAndIdNot(String name, Integer code, Integer id);
}
