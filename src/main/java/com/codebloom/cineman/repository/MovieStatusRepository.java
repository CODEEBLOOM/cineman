package com.codebloom.cineman.repository;

import com.codebloom.cineman.common.enums.MovieStatus;
import com.codebloom.cineman.model.MovieStatusEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface MovieStatusRepository extends JpaRepository<MovieStatusEntity, String> {

}
