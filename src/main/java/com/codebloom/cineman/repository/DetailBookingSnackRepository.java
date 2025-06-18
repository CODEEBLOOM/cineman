package com.codebloom.cineman.repository;

import com.codebloom.cineman.model.DetailBookingSnackEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DetailBookingSnackRepository extends JpaRepository<DetailBookingSnackEntity, Long> {

}
