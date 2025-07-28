package com.codebloom.cineman.service.impl;

import com.codebloom.cineman.common.enums.InvoiceStatus;
import com.codebloom.cineman.controller.request.DetailBookingSnackRequest;
import com.codebloom.cineman.controller.response.DetailBookingSnackResponse;
import com.codebloom.cineman.exception.DataNotFoundException;
import com.codebloom.cineman.model.DetailBookingSnackEntity;
import com.codebloom.cineman.model.InvoiceEntity;
import com.codebloom.cineman.model.SnackEntity;
import com.codebloom.cineman.repository.DetailBookingSnackRepository;
import com.codebloom.cineman.repository.InvoiceRepository;
import com.codebloom.cineman.repository.SnackRepository;
import com.codebloom.cineman.service.DetailBookingSnackService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DetailBookingSnackServiceImpl implements DetailBookingSnackService {

    private final DetailBookingSnackRepository detailRepository;
    private final SnackRepository snackRepository;
    private final InvoiceRepository invoiceRepository;
    private final ModelMapper mapper;

    /**
     * Create detail booking snack
     * @param request DetailBookingSnackRequest
     * @return DetailBookingSnackResponse
     */
    @Override
    public DetailBookingSnackResponse create(DetailBookingSnackRequest request) {
        InvoiceEntity invoice = invoiceRepository.findByIdAndStatusNot(request.getInvoiceId(), InvoiceStatus.CANCELLED)
                .orElseThrow(() -> new DataNotFoundException("Invoice not found"));
        SnackEntity snack = snackRepository.findByIdAndIsActive(request.getSnackId(), true)
                .orElseThrow(() -> new DataNotFoundException("Snack not found"));

        DetailBookingSnackEntity detail = DetailBookingSnackEntity.builder()
                .snack(snack)
                .totalSnack(request.getTotalSnack())
                .invoice(invoice)
                .build();

        DetailBookingSnackEntity saved = detailRepository.save(detail);
        return convert(saved);
    }

    /**
     * Update detail booking ticket
     * @param id id of detail booking snack
     * @param request DetailBookingSnackRequest
     * @return DetailBookingSnackResponse
     */
    @Override
    @Transactional
    public DetailBookingSnackResponse update(Long id, DetailBookingSnackRequest request) {
        DetailBookingSnackEntity detail = detailRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("Detail not found"));
        detail.setTotalSnack(request.getTotalSnack());
        detailRepository.save(detail);
        return convert(detail);
    }

    /**
     * Create multiple details
     * @param requests List<DetailBookingSnackRequest>
     * @return List<DetailBookingSnackResponse>
     */
    @Override
    public List<DetailBookingSnackResponse> createMultiple(List<DetailBookingSnackRequest> requests) {
        if (requests.isEmpty()) return null;
        detailRepository.deleteByInvoiceId(requests.get(0).getInvoiceId());
        List<DetailBookingSnackResponse> responses = new ArrayList<>();
        requests.forEach(request -> responses.add(create(request)));
        return responses.isEmpty() ? null : responses;
    }


    /**
     * Convert DetailBookingSnackEntity to DetailBookingSnackResponse
     * @param entity DetailBookingSnackEntity
     * @return DetailBookingSnackResponse
     */
    private DetailBookingSnackResponse convert(DetailBookingSnackEntity entity) {
         DetailBookingSnackResponse response = mapper.map(entity, DetailBookingSnackResponse.class);
         Double totalMoney = entity.getTotalSnack() * entity.getSnack().getUnitPrice();
         response.setTotalMoney(totalMoney);
         return response;
    }
}