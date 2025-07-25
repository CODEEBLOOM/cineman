package com.codebloom.cineman.repository;

import com.codebloom.cineman.model.DetailBookingSnackEntity;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DetailBookingSnackRepository extends JpaRepository<DetailBookingSnackEntity, Long> {

    @Transactional
    @Modifying
    @Query(value = "DELETE FROM DetailBookingSnackEntity db  WHERE db.invoice.id = :invoiceId")
    void deleteByInvoiceId(Long invoiceId);

    @Query(value = "SELECT db FROM DetailBookingSnackEntity db  WHERE db.invoice.id = :invoiceId AND db.snack.id = :snackId")
    Optional<DetailBookingSnackEntity> findBySnackIdAndInvoiceId( Long invoiceId, Integer snackId);
}
