package com.codebloom.cineman.repository;

import com.codebloom.cineman.common.enums.SatisfactionLevel;
import com.codebloom.cineman.model.FeedbackEntity;
import com.codebloom.cineman.model.UserEntity;
import com.codebloom.cineman.common.enums.UserStatus;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FeedbackRepository extends JpaRepository<FeedbackEntity, Integer> {

    // Tìm feedback theo email của người dùng thông qua invoice - customer (email)
    List<FeedbackEntity> findByInvoice_Customer_Email(String email);

    // Tìm feedback theo mức độ hài lòng
    List<FeedbackEntity> findBySatisfactionLevel(SatisfactionLevel level);

    // Kiểm tra xem invoice đã có feedback chưa
    boolean existsByInvoice_InvoiceId(Long invoiceId);

    // Tìm feedback theo user (customer) và status (ACTIVE)
    List<FeedbackEntity> findByInvoiceCustomerAndInvoiceCustomerStatus(UserEntity customer, UserStatus status);
    
}
