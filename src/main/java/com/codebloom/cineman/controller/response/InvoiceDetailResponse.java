package com.codebloom.cineman.controller.response;

import com.codebloom.cineman.common.enums.InvoiceStatus;
import com.codebloom.cineman.common.enums.PaymentMethod;
import com.codebloom.cineman.model.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
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
    private Double totalMoneyPromotion;
    private InvoiceStatus status;
    private UserEntity customer;
    private UserEntity staff;
    private PromotionEntity promotion;
    private ShowTimeEntity showTime;
    private MovieEntity movie;
    private MovieTheaterEntity movieTheater;
    private CinemaTheaterEntity cinemaTheater;
    private List<TicketResponse> tickets;
    private List<DetailBookingSnackResponse> detailBookingSnacks;
    private Date createdAt;
    private Date updatedAt;
}
