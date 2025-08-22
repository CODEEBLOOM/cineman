package com.codebloom.cineman.service;


import com.codebloom.cineman.controller.request.UserPointHistoryRequest;
import com.codebloom.cineman.controller.response.UserPointHistoryResponse;
import com.codebloom.cineman.model.InvoiceEntity;

import java.util.List;

public interface UserPointHistoryService {

    UserPointHistoryResponse createTransaction(UserPointHistoryRequest request);

    UserPointHistoryResponse refundTransaction(UserPointHistoryRequest request, String vnTnxRef);

    UserPointHistoryResponse earnPoints(InvoiceEntity invoice);

    List<UserPointHistoryResponse> getAllHistory(long userId);

}
