package com.codebloom.cineman.controller.request;

import com.codebloom.cineman.common.enums.PaymentMethod;
import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class InvoiceUpdateRequest {

    @NotBlank(message = "User's email is is not blank !")
    @NotNull( message = "User's email is is not null !")
    @Email(regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$", message = "User's email invalid !")
    private String email;

    @Size(min = 1, max = 20, message = "Phone number is must be less than or equal 20 character !")
    private String phoneNumber;

    @Builder.Default
    private PaymentMethod paymentMethod = PaymentMethod.BANK_TRANSFER;

    @Min(value = 1, message = "Total ticket is must be greater than 0")
    @NotNull(message = "Total ticket is must not null !")
    private Integer totalTicket;

    private Long customerId;
    private Long staffId;
    private Long promotionId;
}
