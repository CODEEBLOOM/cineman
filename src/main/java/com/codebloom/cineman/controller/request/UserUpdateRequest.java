package com.codebloom.cineman.controller.request;

import com.codebloom.cineman.common.enums.GenderUser;
import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.Set;

@Getter
@Setter
@Builder
public class UserUpdateRequest {

    @NotNull( message = "Id người dùng không được phép null !")
    @Min( value = 1, message = "Id người dùng phải lớn hơn 0 !")
    private Long userId;

    @NotNull( message = "Email khách hàng không được phép null !")
    @NotBlank ( message = "Email khách hàng không được phép trống !")
    @Email(regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$", message = "User's email invalid !")
    private String email;

    @NotNull( message = "Họ và tên khách hàng không được phép null !")
    @NotBlank ( message = "Họ và tên khách hàng không được để trống !")
    @Size(min = 1, max = 100, message = "Họ và tên khách hàng tối đa 100 kí tự !")
    private String fullName;

    @NotNull( message = "Số đồ khách hàng không được phép null !")
    @NotBlank ( message = "Số đồ khách hàng không được phép trống !")
    @Size(min = 9, max = 20, message = "Số đồ khách hàng phải lớn hơn 10 kí tự !")
    @Pattern(regexp = "^0[0-9]{9,10}$", message = "User's phone number invalid !")
    private String phoneNumber;

    @Size(max = 200, message = "Địa chỉ khách hàng tối đa 200 kí tự !")
    private String address;

    @Size(max = 100, message = "Phường/Xã tối đa 100 kí tự !")
    private String ward;

    @Size(max = 100, message = "Tỉnh/Thành phố tối đa 100 kí tự !")
    private String province;

    @Past(message = "Ngày sinh phải nhỏ hơn ngày hôm nay !")
    private Date dateOfBirth;

    @NotNull( message = "Giới tính khách hàng không được phép null !")
    private GenderUser gender;
    private String avatar;
    private Set<String> roleIds;

}
