package com.codebloom.cineman.controller.response;

import com.codebloom.cineman.common.enums.InvoiceStatus;
import com.codebloom.cineman.common.enums.PaymentMethod;
import com.codebloom.cineman.model.TicketEntity;
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
    private Long promotionId;
    private Date createdAt;
    private Date updatedAt;
    private List<TicketEntity> tickets;

}
