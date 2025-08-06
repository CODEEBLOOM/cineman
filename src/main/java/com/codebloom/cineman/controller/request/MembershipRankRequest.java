package com.codebloom.cineman.controller.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class MembershipRankRequest {

    @NotNull(message = "Tên của membership rank không được phép null !")
    @Size(min = 1, max = 50, message = "Tên của membership rank tối đa 50 kí tự !")
    private String name;

    @NotNull(message = "Số điểm tối thiểu của membership rank không được phép null !")
    @Min(value = 0, message = "Số điểm tối thiểu của membership rank phải lớn hơn 0 !")
    private Integer requiredPoint;

    @NotNull(message = "Phần trăm quy đổi điểm của membership rank không được phép null !")
    @Min(value = 0, message = "Phần trăm quy đổi điểm của membership rank phải lớn hơn 0 !")
    private Double returnPointsTicket;

    @NotNull(message = "Phần trăm quy đổi điểm sách snack của membership rank không được phép null !")
    @Min(value = 0, message = "Phần trăm quy đổi điểm sách snack của membership rank phải lớn hơn 0 !")
    private Double returnPointsSnack;

    @NotNull(message = "Cấp của membership rank không được phép null !")
    private Integer priorityLevel;

}
