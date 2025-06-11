package com.codebloom.cineman.repository;

import com.codebloom.cineman.model.DetailBookingSnackEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DetailBookingSnackRepository extends JpaRepository<DetailBookingSnackEntity, Long> {

}
