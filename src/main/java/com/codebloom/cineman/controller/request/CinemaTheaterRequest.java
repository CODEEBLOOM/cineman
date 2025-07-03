package com.codebloom.cineman.controller.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CinemaTheaterRequest {

    @NotNull(message = "Tên của phòng chiếu không được phép null !")
    @Size(min = 1 , max = 100, message = "Tên của phòng chiếu phải tử 1 - 100 kí tự")
    String name;

    @NotNull(message = "Số lượng hàng ghế không được phép trống !")
    @Min(value =  1, message = "Số lượng hàng ghế phải lớn hơn 0 !")
    Integer numberOfRows;

    @NotNull(message = "Số lượng cột không được phép trống !")
    @Min(value =  1, message = "Số lượng cột phải lớn hơn 0 !")
    Integer numberOfColumns;

    @NotNull(message = "Số lượng hàng ghế thường không được phép trống !")
    @Min(value =  1, message = "Số lượng hàng thường ghế phải lớn hơn 0 !")
    Integer regularSeatRow;

    @NotNull(message = "Số lượng hàng ghế thường không được phép trống !")
    @Min(value =  1, message = "Số lượng hàng ghế thường phải lớn hơn 0 !")
    Integer vipSeatRow;

    @NotNull(message = "Số lượng hàng ghế thường không được phép trống !")
    @Min(value =  1, message = "Số lượng hàng ghế thường phải lớn hơn 0 !")
    Integer doubleSeatRow;

    @NotNull(message = "Id của loại phòng chiếu không được phép trống !")
    @Min(value =  1, message = "Id của loại phòng chiếu phải lớn hơn 0 !")
    Integer cinemaTypeId;

    @NotNull(message = "Id của rạp chiếu không được phép trống !")
    @Min(value =  1, message = "Id của rạp chiếu phải lớn hơn 0 !")
    Integer movieTheaterId;

}
