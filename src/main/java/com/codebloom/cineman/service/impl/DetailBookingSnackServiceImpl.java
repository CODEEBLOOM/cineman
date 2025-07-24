package com.codebloom.cineman.service.impl;

import com.codebloom.cineman.controller.request.DetailBookingSnackRequest;
import com.codebloom.cineman.controller.response.DetailBookingSnackResponse;
import com.codebloom.cineman.exception.*;
import com.codebloom.cineman.model.DetailBookingSnackEntity;
import com.codebloom.cineman.model.InvoiceEntity;
import com.codebloom.cineman.model.SnackEntity;
import com.codebloom.cineman.repository.DetailBookingSnackRepository;
import com.codebloom.cineman.repository.InvoiceRepository;
import com.codebloom.cineman.repository.SnackRepository;
import com.codebloom.cineman.service.DetailBookingSnackService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DetailBookingSnackServiceImpl implements DetailBookingSnackService {

    private final DetailBookingSnackRepository detailRepository;
    private final SnackRepository snackRepository;
    private final InvoiceRepository invoiceRepository;
    private final ModelMapper mapper;

    @Override
    public DetailBookingSnackResponse addToInvoice(Long invoiceId, DetailBookingSnackRequest request) {
        InvoiceEntity invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new DataNotFoundException("Invoice not found"));
        SnackEntity snack = snackRepository.findById(request.getSnackId())
                .orElseThrow(() -> new DataNotFoundException("Snack not found"));

        DetailBookingSnackEntity detail = DetailBookingSnackEntity.builder()
                .snack(snack)
                .totalSnack(request.getTotalSnack())
                .invoice(invoice)
                .build();

        DetailBookingSnackEntity saved = detailRepository.save(detail);
        return mapper.map(saved, DetailBookingSnackResponse.class);
    }
}