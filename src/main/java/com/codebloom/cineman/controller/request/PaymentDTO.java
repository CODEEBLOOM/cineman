package com.codebloom.cineman.controller.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@Builder
public class PaymentDTO {
    @JsonProperty("amount")
    private Long amount; // Số tiền cần thanh toán

    @JsonProperty("bankCode")
    private String bankCode; // Mã ngân hàng
}
