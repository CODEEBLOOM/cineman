package com.codebloom.cineman.controller.request;

import com.codebloom.cineman.common.enums.InvoiceStatus;
import com.codebloom.cineman.common.enums.PaymentMethod;
import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class InvoiceCreateRequest {

    @NotBlank(message = "User's email is is not blank !")
    @NotNull( message = "User's email is is not null !")
    @Email(regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$", message = "User's email invalid !")
    private String email;

    @Pattern(regexp = "^(|0[0-9]{9,10}+)$", message = "Phone number is incorrect format !")
    private String phoneNumber;
    private PaymentMethod paymentMethod;
    private Long customerId;
    private Long staffId;


    private InvoiceStatus invoiceStatus = InvoiceStatus.PENDING;

    @Min(value = 1, message = "Id's movie is must be greater than 0")
    private Integer totalTicket ;

}
