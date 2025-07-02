package com.codebloom.cineman.repository;

import com.codebloom.cineman.common.enums.SatisfactionLevel;
import com.codebloom.cineman.model.FeedbackEntity;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FeedbackRepository extends JpaRepository<FeedbackEntity, Integer> {

    // Tìm feedback theo email của người dùng thông qua invoice - customer (email)
    List<FeedbackEntity> findByInvoice_Customer_Email(String email);

    List<FeedbackEntity> findBySatisfactionLevel(SatisfactionLevel level);

    boolean existsByInvoice_InvoiceId(Long invoiceId);
}
