package com.codebloom.cineman.repository;

import com.codebloom.cineman.model.CinemaTheaterEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CinemaTheatersRepository extends JpaRepository<CinemaTheaterEntity,Integer > {
}
