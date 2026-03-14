package com.codebloom.cineman.service.impl;

import com.codebloom.cineman.common.enums.InvoiceStatus;
import com.codebloom.cineman.controller.request.DetailBookingSnackRequest;
import com.codebloom.cineman.controller.request.DetailBookingSnackUpdateRequest;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class DetailBookingSnackServiceImpl implements DetailBookingSnackService {

    private final DetailBookingSnackRepository detailRepository;
    private final SnackRepository snackRepository;
    private final InvoiceRepository invoiceRepository;
    private final ModelMapper mapper;

    /**
     * Create detail booking snack
     *
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
                .totalMoney(snack.getUnitPrice() * request.getTotalSnack())
                .invoice(invoice)
                .build();
        invoice.setTotalAmount(invoice.getTotalAmount() + detail.getTotalMoney());
        DetailBookingSnackEntity saved = detailRepository.save(detail);
        return convert(saved);
    }

    /**
     * Update detail booking ticket
     *
     * @param id      id of detail booking snack
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
     *
     * @param requests List<DetailBookingSnackRequest>
     * @return List<DetailBookingSnackResponse>
     */
    @Override
    public List<DetailBookingSnackResponse> createMultiple(List<DetailBookingSnackRequest> requests) {
        if (requests.isEmpty()) return null;

        this.clearByInvoiceId(requests.get(0).getInvoiceId());
        List<DetailBookingSnackResponse> responses = new ArrayList<>();
        requests.forEach(request -> {
            if (request.getTotalSnack() > 0) {
                responses.add(create(request));
            }
        });
        return responses.isEmpty() ? null : responses;
    }

    @Override
    public void clearByInvoiceId(Long invoiceId) {
        InvoiceEntity invoice = invoiceRepository.findByIdAndStatusNot(invoiceId, InvoiceStatus.CANCELLED)
                .orElseThrow(() -> new DataNotFoundException("Invoice not found"));
        double totalMoneySnack = 0;
        for (DetailBookingSnackEntity detail : invoice.getDetailBookingSnacks()) {
            totalMoneySnack += detail.getTotalMoney();
            detailRepository.delete(detail);
        }
        invoice.setTotalAmount(invoice.getTotalAmount() - totalMoneySnack);
        invoice.setDetailBookingSnacks(null);
        invoiceRepository.save(invoice);
    }

    @Transactional
    public DetailBookingSnackResponse update(DetailBookingSnackUpdateRequest request) {
        if (request.getId() == null) {
            DetailBookingSnackRequest detailBookingSnackRequest = DetailBookingSnackRequest.builder()
                    .invoiceId(request.getInvoiceId())
                    .snackId(request.getSnackId())
                    .totalSnack(request.getQuantity())
                    .build();
            return create(detailBookingSnackRequest);
        } else {
            DetailBookingSnackEntity detail = detailRepository.findById(request.getId())
                    .orElseThrow(() -> new DataNotFoundException("Detail not found"));
            InvoiceEntity invoice = invoiceRepository.findByIdAndStatusNot(request.getInvoiceId(), InvoiceStatus.CANCELLED)
                    .orElseThrow(() -> new DataNotFoundException("Invoice not found"));
            SnackEntity snack = snackRepository.findByIdAndIsActive(request.getSnackId(), true)
                    .orElseThrow(() -> new DataNotFoundException("Snack not found"));
            detail.setSnack(snack);
            detail.setInvoice(invoice);
            detail.setTotalSnack(request.getQuantity());
            detail.setTotalMoney(detail.getTotalSnack() * detail.getSnack().getUnitPrice());
            return convert(detailRepository.save(detail));
        }
    }

    @Override
    public List<DetailBookingSnackResponse> updateMultiple(List<DetailBookingSnackRequest> requests) {
        if (requests.isEmpty()) return null;
        InvoiceEntity invoice = invoiceRepository.findByIdAndStatusNot(requests.get(0).getInvoiceId(), InvoiceStatus.CANCELLED)
                .orElseThrow(() -> new DataNotFoundException("Không tìm thấy hóa đơn"));
        List<DetailBookingSnackResponse> responses = new ArrayList<>();
        Map<String, DetailBookingSnackEntity> detailMap = new HashMap<>();
        invoice.getDetailBookingSnacks().forEach(detail -> {
            detailMap.put(detail.getSnack().getId() + "-" + detail.getInvoice().getId(), detail);
        });

        requests.forEach(request -> {
            String key = request.getSnackId() + "-" + request.getInvoiceId();
//            Đã chọn combo trước đó
            if (detailMap.containsKey(key)) {
                DetailBookingSnackEntity detail = detailMap.get(key);
                int newTotal = request.getTotalSnack() + detail.getTotalSnack();
                detail.setTotalSnack(request.getTotalSnack() + detail.getTotalSnack());
                detail.setTotalMoney(detail.getTotalSnack() * detail.getSnack().getUnitPrice());
                responses.add(convert(detailRepository.save(detail)));
                return;
            }
            DetailBookingSnackResponse response = create(
                    DetailBookingSnackRequest.builder()
                            .invoiceId(request.getInvoiceId())
                            .snackId(request.getSnackId())
                            .totalSnack(request.getTotalSnack())
                            .build()
            );
            responses.add(response);
        });

        double totalMoneySnack = 0.0;
        for (DetailBookingSnackResponse response : responses) {
            totalMoneySnack += response.getTotalMoney();
        }

        invoice.setTotalAmount(totalMoneySnack + invoice.getTotalAmount());
        invoiceRepository.save(invoice);
        return responses.isEmpty() ? null : responses;
    }


    /**
     * Convert DetailBookingSnackEntity to DetailBookingSnackResponse
     *
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