package com.codebloom.cineman.service;

import com.codebloom.cineman.controller.request.InvoiceCreateRequest;
import com.codebloom.cineman.controller.request.InvoiceUpdateRequest;
import com.codebloom.cineman.controller.response.InvoiceResponse;
import com.codebloom.cineman.model.InvoiceEntity;

public interface InvoiceService {

    InvoiceResponse create(InvoiceCreateRequest invoice, Long showTimeId);

    InvoiceResponse update(Long id, InvoiceUpdateRequest invoice);

    Double getTotalMoney(Long invoiceId);

}
