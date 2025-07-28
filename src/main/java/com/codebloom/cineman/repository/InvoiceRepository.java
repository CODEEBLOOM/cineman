package com.codebloom.cineman.repository;

import com.codebloom.cineman.common.enums.InvoiceStatus;
import com.codebloom.cineman.common.enums.ShowTimeStatus;
import com.codebloom.cineman.model.InvoiceEntity;
import com.codebloom.cineman.model.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InvoiceRepository extends JpaRepository<InvoiceEntity, Long> {

    Optional<InvoiceEntity> findByIdAndStatus(Long id, InvoiceStatus status);

    @Query("""
            SELECT i FROM InvoiceEntity i INNER JOIN i.tickets t ON t.invoice.id = i.id
                        WHERE t.showTime.id = :showTimeId AND i.status = :status AND t.showTime.status = :showTimeStatus
            """)
    Optional<InvoiceEntity> findByShowTimeIdAndStatus(Long showTimeId, InvoiceStatus status, ShowTimeStatus showTimeStatus);

    List<InvoiceEntity> findByCustomerAndStaffAndStatus(UserEntity customer, UserEntity staff, InvoiceStatus status);

    Optional<InvoiceEntity> findByIdAndStatusNot(Long id, InvoiceStatus status);

    Optional<InvoiceEntity> findByVnTxnRefAndStatus(String vnTxnRef, InvoiceStatus invoiceStatus);

    Optional<InvoiceEntity> findByQrCode(String qrCode);

    @Query("""
            SELECT i FROM InvoiceEntity i INNER JOIN TicketEntity t ON t.invoice.id = i.id
                        WHERE (i.customer.userId = :id OR i.staff.userId = :id)
                                    AND t.showTime.id = :showTimeId 
                                    AND i.status = :status
            """)
    Optional<InvoiceEntity> findByUserIdAndShowTimeIdAndStatus(Long id, Long showTimeId, InvoiceStatus status);

    @Query("""
            SELECT i FROM InvoiceEntity i
                        WHERE i.customer.userId = :id OR i.staff.userId = :id
            """)
    List<InvoiceEntity> findByCustomerOrStaff(Long id);
}
