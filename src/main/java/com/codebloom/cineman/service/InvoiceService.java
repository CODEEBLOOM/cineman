package com.codebloom.cineman.service;

import com.codebloom.cineman.controller.request.InvoiceCreateRequest;
import com.codebloom.cineman.controller.request.InvoiceUpdateRequest;
import com.codebloom.cineman.model.InvoiceEntity;

public interface InvoiceService {

    InvoiceEntity create(InvoiceCreateRequest invoice);

    InvoiceEntity update(Long id, InvoiceUpdateRequest invoice);

}
