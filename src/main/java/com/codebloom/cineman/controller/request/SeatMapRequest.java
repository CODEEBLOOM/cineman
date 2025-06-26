package com.codebloom.cineman.controller.request;


import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SeatMapRequest {

    @NotBlank(message = "Tên sơ đồ ghế không được trống !")
    @Size(min = 1, max = 150 , message = "Tên sơ đồ ghế nhỏ hơn hoặc bằng 150 kí tự !")
    private String name;

    @NotNull(message = "Số hàng của sơ đồ ghế không được phép null !")
    @Min(value = 1, message = "Số hàng của sơ đồ ghế phải lớn hơn 0 !")
    private Integer numberOfRows;

    @NotNull(message = "Số cột của sơ đồ ghế không được phép null !")
    @Min(value = 1, message = "Số cột của sơ đồ ghế phải lớn hơn 0 !")
    private Integer numberOfColumns;

    @NotNull(message = "Số hàng ghế thương của sơ đồ ghế không được phép null !")
    @Min(value = 1, message = "Số hàng ghế thương của sơ đồ ghế phải lớn hơn 0 !")
    private Integer regularSeatRow;

    @NotNull(message = "Số hàng ghế vip của sơ đồ ghế không được phép null !")
    @Min(value = 1, message = "Số hàng ghế vip của sơ đồ ghế phải lớn hơn 0 !")
    private Integer vipSeatRow;

    @NotNull(message = "Số hàng ghế đôi của sơ đồ ghế không được phép null !")
    @Min(value = 1, message = "Số hàng ghế đôi của sơ đồ ghế phải lớn hơn 0 !")
    private Integer doubleSeatRow;

    @Size( max = 250 , message = "Tên sơ đồ ghế nhỏ hơn hoặc bằng 250 kí tự !")
    private String description;

    private Boolean status;

}
