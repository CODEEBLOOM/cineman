package com.codebloom.cineman.repository;

import com.codebloom.cineman.common.enums.TicketStatus;
import com.codebloom.cineman.model.InvoiceEntity;
import com.codebloom.cineman.model.SeatEntity;
import com.codebloom.cineman.model.ShowTimeEntity;
import com.codebloom.cineman.model.TicketEntity;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


import java.util.List;
import java.util.Optional;

@Repository
public interface TicketRepository extends JpaRepository<TicketEntity, Long> {

    List<TicketEntity> findByInvoice(InvoiceEntity invoice);

    List<TicketEntity> findByShowTime(ShowTimeEntity showTime);

    /**
     * Xóa tất cả các vé quá thời gian chờ hợp lệ
     * @param ticketStatus Trạng thái vé -> PENDING
     */
    @Modifying
    @Transactional
    @Query(value = """
        DELETE FROM tickets
        WHERE status = :ticketStatus
          AND DATEADD(MINUTE, 10, create_booking) < GETDATE()
    """, nativeQuery = true)
    void deleteAllTicketOutOfLimitTime(@Param("ticketStatus") TicketStatus ticketStatus);

    Optional<TicketEntity> findByIdAndStatus(Long id, TicketStatus ticketStatus);

    Optional<TicketEntity> findByShowTimeAndSeat(ShowTimeEntity showTimeEntity, SeatEntity seatEntity);
}
