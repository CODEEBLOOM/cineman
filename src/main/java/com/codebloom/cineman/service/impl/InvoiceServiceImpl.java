package com.codebloom.cineman.service.impl;

import com.codebloom.cineman.controller.response.InvoiceResponse;
import com.codebloom.cineman.model.TicketEntity;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Date;
import java.util.Objects;
import java.util.Optional;

import com.codebloom.cineman.common.enums.InvoiceStatus;
import com.codebloom.cineman.controller.request.InvoiceCreateRequest;
import com.codebloom.cineman.controller.request.InvoiceUpdateRequest;
import com.codebloom.cineman.exception.DataNotFoundException;
import com.codebloom.cineman.model.InvoiceEntity;
import com.codebloom.cineman.model.UserEntity;
import com.codebloom.cineman.repository.InvoiceRepository;
import com.codebloom.cineman.repository.UserRepository;
import com.codebloom.cineman.service.InvoiceService;

@Slf4j(topic = "INVOICE_SERVICE")
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
    public InvoiceResponse create(InvoiceCreateRequest invoice) {
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
        return toInvoiceResponse(invoiceRepository.save(invoiceEntity));
    }

    /**
     * Cập nhật hóa đơn
     * @param id id hóa đơn
     * @param invoice thống tin hóa đơn
     * @return InvoiceEntity
     */
    @Override
    public InvoiceResponse update(Long id, InvoiceUpdateRequest invoice) {
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

        return toInvoiceResponse(invoiceRepository.save(invoiceEntity));
    }

    private InvoiceEntity getInvoice(UserEntity customer, UserEntity staff ){
        return invoiceRepository.findByCustomerAndStaffAndStatus(customer, staff, InvoiceStatus.PENDING)
                .orElse(null);
    }

    /**
     * Chuyển đổi từ InvoiceEntity sang InvoiceResponse
     * @param invoice InvoiceEntity
     * @return InvoiceResponse
     */
    private InvoiceResponse toInvoiceResponse(InvoiceEntity invoice) {
        return InvoiceResponse.builder()
                .id(invoice.getId())
                .email(invoice.getEmail())
                .phoneNumber(invoice.getPhoneNumber())
                .paymentMethod(invoice.getPaymentMethod())
                .totalTicket(invoice.getTotalTicket())
                .customerId(invoice.getCustomer() != null ? invoice.getCustomer().getUserId() : null)
                .staffId(invoice.getStaff() != null ? invoice.getStaff().getUserId() : null)
                .status(invoice.getStatus())
                .totalMoney(
                        Optional.ofNullable(invoice.getTickets())
                                .orElse(Collections.emptyList())
                                .stream()
                                .map(TicketEntity::getPrice)
                                .filter(Objects::nonNull)
                                .reduce(0.0, Double::sum)
                )
                .createdAt(invoice.getCreatedAt())
                .updatedAt(invoice.getUpdatedAt())
                .build();
    }

}
