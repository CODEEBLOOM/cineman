package com.codebloom.cineman.service;

import com.codebloom.cineman.controller.request.DetailBookingSnackRequest;
import com.codebloom.cineman.controller.response.DetailBookingSnackResponse;


public interface DetailBookingSnackService {
    DetailBookingSnackResponse addToInvoice(Long invoiceId, DetailBookingSnackRequest request);
 }
