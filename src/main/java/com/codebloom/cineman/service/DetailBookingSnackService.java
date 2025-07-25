package com.codebloom.cineman.service;

import com.codebloom.cineman.controller.request.DetailBookingSnackRequest;
import com.codebloom.cineman.controller.response.DetailBookingSnackResponse;

import java.util.List;


public interface DetailBookingSnackService {
    DetailBookingSnackResponse create(DetailBookingSnackRequest request);

    DetailBookingSnackResponse update(Long id, DetailBookingSnackRequest request);

    List<DetailBookingSnackResponse> createMultiple(List<DetailBookingSnackRequest> requests);

 }
