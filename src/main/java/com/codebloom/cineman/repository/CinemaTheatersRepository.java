package com.codebloom.cineman.repository;

import com.codebloom.cineman.model.CinemaTheatersEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CinemaTheatersRepository extends JpaRepository<CinemaTheatersEntity,Integer > {
}
