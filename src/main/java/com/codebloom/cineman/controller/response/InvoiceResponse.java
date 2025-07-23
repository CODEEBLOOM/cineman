package com.codebloom.cineman.controller.response;

import com.codebloom.cineman.common.enums.InvoiceStatus;
import com.codebloom.cineman.common.enums.PaymentMethod;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@Builder
public class InvoiceResponse {

    private Long id;
    private String email;
    private String phoneNumber;
    private PaymentMethod paymentMethod;
    private Integer totalTicket;
    private Long customerId;
    private Long staffId;
    private Double totalMoney;
    private Double totalMoneyTicket;
    private InvoiceStatus status;
    private Date createdAt;
    private Date updatedAt;

}
