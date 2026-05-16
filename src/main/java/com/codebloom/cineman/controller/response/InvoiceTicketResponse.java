package com.codebloom.cineman.controller.response;

import com.codebloom.cineman.model.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceTicketResponse {

    private InvoiceEntity invoiceEntity;
    private ShowTimeEntity showTimeEntity;
    private MovieEntity movieEntity;
    private List<DetailBookingSnackResponse> detailBookingSnackResponse;
    private int changePoint;
    private double changeMoney;
    private int savePoint;
    private PromotionEntity promotionEntity;
    private UserResponse customer;

}
