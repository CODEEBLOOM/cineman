package com.codebloom.cineman.repository;

import com.codebloom.cineman.model.DetailBookingSnackEntity;
import com.codebloom.cineman.model.Id.DetailBookingSnackId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DetailBookingSnackRepository extends JpaRepository<DetailBookingSnackEntity, DetailBookingSnackId> {

}
