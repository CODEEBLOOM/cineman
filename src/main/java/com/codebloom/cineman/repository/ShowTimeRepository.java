package com.codebloom.cineman.repository;


import com.codebloom.cineman.model.ShowTimeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ShowTimeRepository extends JpaRepository<ShowTimeEntity, Long> {
}
