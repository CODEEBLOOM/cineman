package com.codebloom.cineman.controller.request;

import com.codebloom.cineman.common.enums.SeatStatus;
import com.codebloom.cineman.common.enums.SeatType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class SeatRequest {

    @Min(value = 0 , message = "Chỉ số cột của ghế phải lớn hơn 0 !")
    @NotNull(message = "Chỉ số cột của ghế không được phép null !")
    private Integer columnIndex;

    @Min(value = 0 , message = "Chỉ số hàng của ghế phải lớn hơn 0 !")
    @NotNull(message = "Chỉ số hàng của ghế không được phép null !")
    private Integer rowIndex;

    @Size(min = 1, max = 10, message = "Nhãn của ghế có số kí tự nhỏ hơn 10 kí tự !")
    private String label;

    @Size(min = 1, max = 25, message = "Tên loại ghế phải từ 1 đến 25 ký tự")
    @NotNull(message = "Id của loại ghế không được phép null !")
    private String seatType;

    @NotNull(message = "Id của phòng chiếu không được phép trống !")
    @Min(value = 1 , message = "Id của phòng chiếu phải lớn hơn 0 !")
    private Integer cinemaTheaterId;

    private SeatStatus status = SeatStatus.ACTIVE;

}
