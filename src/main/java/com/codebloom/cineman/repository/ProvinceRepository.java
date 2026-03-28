package com.codebloom.cineman.repository;

import com.codebloom.cineman.model.ProvinceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProvinceRepository extends JpaRepository<ProvinceEntity, Integer> {
    Optional<ProvinceEntity> findByName(String name);

    Optional<ProvinceEntity> findByNameAndActive(String name, Boolean active);

    Optional<ProvinceEntity> findByCode(Integer code);

    Optional<ProvinceEntity> findByCodeAndActive(Integer code, Boolean active);

    Optional<ProvinceEntity> findByIdAndActive(Integer id, Boolean active);

    Optional<ProvinceEntity> findByNameAndCodeAndIdNot(String name, Integer code, Integer id);

    Optional<ProvinceEntity> findByNameAndIdNot(String name, Integer id);

    Optional<ProvinceEntity> findByCodeAndIdNot(Integer code, Integer id);

    List<ProvinceEntity> findAllByActive(Boolean active);
}
