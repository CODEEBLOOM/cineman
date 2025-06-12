package com.codebloom.cineman.repository;

import com.codebloom.cineman.model.DirectorEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DirectorRepository extends JpaRepository<DirectorEntity,Integer> {

    @Query("""
    SELECT CASE WHEN COUNT(md) = 0 THEN true ELSE false END
    FROM MovieDirectorEntity md
    WHERE md.director.directorId = :directorId
""")
    boolean isDirectorNotHaveMovie(Integer directorId);
}
