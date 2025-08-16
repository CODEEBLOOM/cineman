package com.codebloom.cineman.controller.response;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class InvoiceDetailPageResponse {
    private List<InvoiceDetailResponse> invoiceDetailResponses;
    private MetaResponse meta;
}
