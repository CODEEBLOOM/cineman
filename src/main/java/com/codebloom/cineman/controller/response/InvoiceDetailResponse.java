package com.codebloom.cineman.controller.response;

import com.codebloom.cineman.common.enums.InvoiceStatus;
import com.codebloom.cineman.common.enums.PaymentMethod;
import com.codebloom.cineman.model.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@Builder
public class InvoiceDetailResponse {
    private Long id;
    private String code;
    private String email;
    private String phoneNumber;
    private PaymentMethod paymentMethod;
    private Integer totalTicket;
    private Double totalMoney;
    private Double totalMoneyTicket;
    private Double totalMoneySnack;
    private Double totalMoneyDiscount;
    private InvoiceStatus status;
    private UserEntity customer;
    private UserEntity staff;
    private PromotionEntity promotion;
    private ShowTimeEntity showTime;
    private MovieEntity movie;
    private MovieTheaterEntity movieTheater;
    private Date createdAt;
    private Date updatedAt;
}
