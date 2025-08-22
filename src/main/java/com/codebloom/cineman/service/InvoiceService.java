package com.codebloom.cineman.service;


import com.codebloom.cineman.controller.request.InvoiceCreateRequest;
import com.codebloom.cineman.controller.request.InvoiceUpdateRequest;
import com.codebloom.cineman.controller.response.InvoiceDetailPageResponse;
import com.codebloom.cineman.controller.response.InvoiceDetailResponse;
import com.codebloom.cineman.controller.response.InvoiceResponse;
import com.codebloom.cineman.controller.response.InvoiceTicketResponse;

import java.util.Date;
import java.util.List;

public interface InvoiceService {

    InvoiceResponse findByUserIdAndShowTimeId(Long id, Long showTimeId);

    InvoiceResponse create(InvoiceCreateRequest invoice, Long showTimeId);

    InvoiceResponse createInvoice(InvoiceCreateRequest invoice);

    InvoiceResponse update(Long id, InvoiceUpdateRequest invoice);

    InvoiceResponse updateTnx(Long id, String tnxRef, Object... args);

    InvoiceResponse updateStatusPaymentSuccess(String txnRef);

    InvoiceResponse applyPromotionToInvoice(Long id, Long promotionId);

    Double getTotalMoney(Long invoiceId);

    InvoiceDetailResponse findByQrCode(String qrCode);

    List<InvoiceDetailResponse> findByUserId(Long userId);

    InvoiceDetailPageResponse findAllInvoicesByCreatedAtAndMovieTheater(Date createdAt, Integer pageNo, Integer pageSize, Integer... movieTheaterId);

    InvoiceTicketResponse  findInvoiceByQRCode(String qrCode);
}
