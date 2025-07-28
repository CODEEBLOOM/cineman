package com.codebloom.cineman.service;


import com.codebloom.cineman.controller.request.InvoiceCreateRequest;
import com.codebloom.cineman.controller.request.InvoiceUpdateRequest;
import com.codebloom.cineman.controller.response.InvoiceResponse;

public interface InvoiceService {

    InvoiceResponse findByUserIdAndShowTimeId(Long id, Long showTimeId);

    InvoiceResponse create(InvoiceCreateRequest invoice, Long showTimeId);
    InvoiceResponse createInvoice(InvoiceCreateRequest invoice);

    InvoiceResponse update(Long id, InvoiceUpdateRequest invoice);

    InvoiceResponse updateTnx(Long id, String tnxRef);

    InvoiceResponse updateStatusPaymentSuccess(String txnRef);

    Double getTotalMoney(Long invoiceId);

}
