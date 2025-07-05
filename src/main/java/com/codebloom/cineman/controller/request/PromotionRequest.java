package com.codebloom.cineman.controller.request;

import com.codebloom.cineman.common.enums.StatusPromotion;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PromotionRequest {
    @NotBlank(message = "Tên không được để trống")
    @Size (max = 100, message = "Tên phải dưới 100 ký tự")
    private String name;

    @NotBlank(message = "Nội dung không được để trống")
    @Size(max = 500, message = "Nội dung phải dưới 500 ký tự")
    private String content;


    private String code;

  //  @FutureOrPresent(message = "Ngày bắt đầu phải là hôm nay hoặc trong tương lai")
    private Date startDay;

    @NotNull(message = "Ngày kết thúc không được để trống")
    @Future(message = "Ngày kết thúc phải trong tương lai")
    private Date endDay;

    @NotNull(message = "Giảm giá không được để trống")
    @DecimalMin(value = "0.0", inclusive = false, message = "Giảm giá phải lớn hơn 0")
    @DecimalMax(value = "100.0", message = "Giảm giá không được vượt quá 100")
    private Double discount;

    @Pattern(regexp = "^(MIN_TOTAL_PRICE|MIN_TOTAL_TICKET|DAY_OF_WEEK)$", message = "Loại điều kiện không hợp lệ")
    private String conditionType;

    @DecimalMin(value = "0.0", message = "Giá trị điều kiện phải >= 0")
    private Double conditionValue;

    private Date conditionDate;

    @NotNull(message = "Trạng thái không được để trống")
    private StatusPromotion status;

    @NotNull(message = "ID nhân viên không được để trống")
    @Positive(message = "ID nhân viên phải là số dương")
    private Long staffId;
}
