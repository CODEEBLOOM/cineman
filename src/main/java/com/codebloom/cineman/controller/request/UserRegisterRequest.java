package com.codebloom.cineman.controller.request;

import com.codebloom.cineman.common.enums.GenderUser;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@Builder
public class UserRegisterRequest {

    @NotNull(message = "Họ và tên không được để trống !")
    @NotBlank(message = "Họ và tên không được để trống !")
    @Size(min = 1, max = 100, message = "Họ và tên tối đa 100 kí tự !")
    private String fullName;

    @NotNull(message = "Email không được để trống !")
    @NotBlank(message = "Email không được để trống !")
    @Size(max = 150, message = "Email tối đa 150 kí tự !")
    @Email(regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$", message = "Email không đúng định dạng !")
    private String email;

    @NotNull(message = "Mật khẩu không được để trống !")
    @NotBlank(message = "Mật khẩu không được để trống !")
    @Size(min = 6, max = 100, message = "Mật khẩu phải có từ 6 đến 100 kí tự !")
    private String password;

    @NotNull(message = "Xác nhận mật khẩu không được để trống !")
    @NotBlank(message = "Xác nhận mật khẩu không được để trống !")
    @Size(min = 6, max = 100, message = "Xác nhận mật khẩu phải có từ 6 đến 100 kí tự !")
    private String confirmPassword;

    @NotNull(message = "Ngày sinh không được để trống !")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @Past(message = "Ngày sinh phải nhỏ hơn ngày hôm nay !")
    private Date dateOfBirth;

    @NotNull(message = "Giới tính không được để trống !")
    private GenderUser gender;

    @NotNull(message = "Số điện thoại không được để trống !")
    @NotBlank(message = "Số điện thoại không được để trống !")
    @Pattern(regexp = "^0[0-9]{9,10}$", message = "Số điện thoại không hợp lệ ! (Phải bắt đầu bằng 0 và có 10-11 chữ số)")
    private String phoneNumber;

    private String address;
    private String facebookId;
    private String googleId;

}
