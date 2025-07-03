package com.codebloom.cineman.repository;

import com.codebloom.cineman.model.InvoiceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface InvoiceRepository extends JpaRepository<InvoiceEntity,Long> {
    @Query("SELECT COALESCE(SUM(i.totalTicket), 0) FROM InvoiceEntity i WHERE i.customer.userId = :customer")
    int countTotalTicketsByCustomerId(@Param("customer") Long customer);
}
