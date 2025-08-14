package com.codebloom.cineman.controller.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class UserPointHistoryResponse {

    private Integer id;
    private Long userId;
    private Long invoiceId;
    private Integer changePoint;
    private String reason;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
