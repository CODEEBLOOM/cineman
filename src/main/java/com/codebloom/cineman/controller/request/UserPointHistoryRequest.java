package com.codebloom.cineman.controller.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class UserPointHistoryRequest {

    @NotNull(message = "Id người dùng không được phép null !")
    @Min( value = 1, message = "Id người dùng phải lớn hơn 0 !")
    private Long userId;

    @Min( value = 1, message = "Id hóa đơn khách hàng phải lớn hơn 0 !")
    private Long invoiceId;

    @NotNull(message = "Số điểm quy đổi của khách hàng không được phép null !")
    private Integer changePoint;

    @NotNull(message = "Lý do quy đổi điểm của khách hàng không được phép null !")
    @NotBlank(message = "Lý do quy đổi điểm của khách hàng không được phép trống !")
    @Size(min = 1, max = 200, message = "Lý do quy đổi điểm của khách hàng tối đa 200 kí tự !")
    private String reason;

}
