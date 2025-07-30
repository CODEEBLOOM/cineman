package com.codebloom.cineman.controller.request;

import com.codebloom.cineman.common.utils.AfterNow;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class PromotionRequest {

    @Size(min = 1, max = 100, message = "Tên của giảm giá không quá 100 ký tự!")
    @NotNull(message = "Tên giảm giá không được phép null !")
    private String name;

    @Size(min = 1, max = 500, message = "Nội dung của giảm giá không quá 500 ký tự!")
    @NotNull(message = "Nội dung của giảm giá không được phép null !")
    private String content;

    @NotNull(message = "Thời điểm bắt đầu không đươc phép null !")
    @AfterNow(message = "Thời điểm bắt đầu phải sau thời điểm hiện tại!")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm:ss")
    private LocalDateTime startDate;

    @Future(message = "Thời điểm kết thúc phải trong tương lai !")
    @AfterNow(message = "Thời điểm bắt đầu phải sau thời điểm hiện tại!")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm:ss")
    private LocalDateTime endDate;

    @NotNull(message = "Phần trăm của giảm giá không được phép null !")
    @Max(value = 1, message = "Phần trăm của giảm giá tối đa 100% !")
    private Double discount;

    @NotNull(message = "Số lượng giảm giá không được phép null !")
    @Min(value = 1, message = "Số lượng giảm giá phải lớn hơn 0 !")
    private Integer quantity;

    @NotNull(message = "Giới hạn tổng tiền của hóa đơn không được phép null !")
    @Min(value = 1, message = "Giới hạn tổng tiền của hóa đơn phải lớn hơn 0 !")
    private Double limitAmount;

    @NotNull(message = "Id nhân viên tạo giảm giá không được phép null !")
    @Min(value = 1, message = "Id nhân viên tạo giảm giá phải lớn hơn 0 !")
    private Long staffId;

}
