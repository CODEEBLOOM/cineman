package com.codebloom.cineman.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Date;

import com.codebloom.cineman.common.enums.InvoiceStatus;
import com.codebloom.cineman.controller.request.InvoiceCreateRequest;
import com.codebloom.cineman.controller.request.InvoiceUpdateRequest;
import com.codebloom.cineman.exception.DataNotFoundException;
import com.codebloom.cineman.model.InvoiceEntity;
import com.codebloom.cineman.model.UserEntity;
import com.codebloom.cineman.repository.InvoiceRepository;
import com.codebloom.cineman.repository.UserRepository;
import com.codebloom.cineman.service.InvoiceService;

@Slf4j
@Service
@RequiredArgsConstructor
public class InvoiceServiceImpl implements InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final UserRepository userRepository;


    /**
     * Tạo mới một hóa đơn
     * @param invoice thông tin hóa đơn
     * @return InvoiceEntity
     */
    @Transactional
    @Override
    public InvoiceEntity create(InvoiceCreateRequest invoice) {
        UserEntity customer = null;
        UserEntity staff = null;

        if(invoice.getCustomerId() == null && invoice.getStaffId() == null) {
            throw new DataNotFoundException("Customer or Staff not found");
        }

        if(invoice.getCustomerId() != null) {
            customer = userRepository.findById(invoice.getCustomerId())
                    .orElseThrow(() -> new DataNotFoundException("Customer not found"));
        }

        if(invoice.getStaffId() != null) {
            staff = userRepository.findById(invoice.getStaffId())
                    .orElseThrow(() -> new DataNotFoundException("Staff not found"));
        }

        // Kiểm tra xem hóa đơn đã có sẵn hay chưa nếu có thì cập nhật không tạo mới một hóa đơn //
        InvoiceEntity invoiceEntity = this.getInvoice(customer, staff);
        if(invoiceEntity != null) {
            Date now = new Date();
            // Cập nhật hóa đơn //
            invoiceEntity.setCustomer(customer);
            invoiceEntity.setStaff(staff);
            invoiceEntity.setEmail(invoice.getEmail());
            invoiceEntity.setPhoneNumber(invoice.getPhoneNumber());
            invoiceEntity.setPaymentMethod(invoice.getPaymentMethod() != null ? invoice.getPaymentMethod() : null);
            invoiceEntity.setTotalTicket(invoice.getTotalTicket() != null ? invoice.getTotalTicket() : 0);
            invoiceEntity.setCreatedAt(now);
            invoiceEntity.setUpdatedAt(now);
        }else {
            // Tạo mới một hóa đơn //
            invoiceEntity = InvoiceEntity.builder()
                    .email(invoice.getEmail())
                    .phoneNumber(invoice.getPhoneNumber())
                    .paymentMethod(invoice.getPaymentMethod() != null ? invoice.getPaymentMethod() : null)
                    .totalTicket(invoice.getTotalTicket() != null ? invoice.getTotalTicket() : 0)
                    .customer(customer)
                    .staff(staff)
                    .status(InvoiceStatus.PENDING)
                    .build();
        }
        return invoiceRepository.save(invoiceEntity);
    }

    /**
     * Cập nhật hóa đơn
     * @param id id hóa đơn
     * @param invoice thống tin hóa đơn
     * @return InvoiceEntity
     */
    @Override
    public InvoiceEntity update(Long id, InvoiceUpdateRequest invoice) {
        UserEntity customer = null;
        UserEntity staff = null;

        if(invoice.getCustomerId() == null && invoice.getStaffId() == null) {
            throw new DataNotFoundException("Customer or Staff not found");
        }


        if(invoice.getCustomerId() != null) {
            customer = userRepository.findById(invoice.getCustomerId())
                    .orElseThrow(() -> new DataNotFoundException("Customer not found"));
        }

        if(invoice.getStaffId() != null) {
            staff = userRepository.findById(invoice.getStaffId())
                    .orElseThrow(() -> new DataNotFoundException("Staff not found"));
        }

        InvoiceEntity invoiceEntity = invoiceRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("Invoice not found"));
        invoiceEntity.setEmail(invoice.getEmail());
        invoiceEntity.setPhoneNumber(invoice.getPhoneNumber());
        invoiceEntity.setPaymentMethod(invoice.getPaymentMethod());
        invoiceEntity.setCustomer(customer);
        invoiceEntity.setStaff(staff);
        invoiceEntity.setTotalTicket(invoice.getTotalTicket());
        invoiceEntity.setUpdatedAt(new Date());

        return invoiceRepository.save(invoiceEntity);
    }

    private InvoiceEntity getInvoice(UserEntity customer, UserEntity staff ){
        return invoiceRepository.findByCustomerOrStaffAndStatus(customer, staff, InvoiceStatus.PENDING)
                .orElse(null);
    }

}
