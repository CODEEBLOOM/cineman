package com.codebloom.cineman.service;

import com.codebloom.cineman.controller.request.PaymentDTO;
import jakarta.servlet.http.HttpServletRequest;

public interface VNPayService {

    String createPaymentUrl(PaymentDTO paymentRequest, HttpServletRequest request);
//    String queryTransaction(PaymentQueryDTO paymentQueryDTO, HttpServletRequest request) throws IOException;
//    String refundTransaction(PaymentRefundDTO refundDTO) throws IOException;

}
