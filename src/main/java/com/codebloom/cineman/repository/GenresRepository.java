package com.codebloom.cineman.repository;

import com.codebloom.cineman.model.GenresEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GenresRepository extends JpaRepository<GenresEntity,Integer> {

    @Query("SELECT COUNT(mg) > 0 FROM MovieGenresEntity mg WHERE mg.genres.genresId = :genresId")
    boolean existsByGenreId(@Param("genresId") Integer genresId);

    Optional<GenresEntity> findByName(String name);
}
