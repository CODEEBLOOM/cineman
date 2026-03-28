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

    @NotNull(message = "Id nguoi dung khong duoc phep null !")
    @Min(value = 1, message = "Id nguoi dung phai lon hon 0 !")
    private Long userId;

    @Min(value = 1, message = "Id hoa don khach hang phai lon hon 0 !")
    private Long invoiceId;

    @NotNull(message = "So diem quy doi cua khach hang khong duoc phep null !")
    @Min(value = 1, message = "So diem quy doi cua khach hang phai lon hon 0 !")
    private Integer changePoint;

    @NotNull(message = "Ly do quy doi diem cua khach hang khong duoc phep null !")
    @NotBlank(message = "Ly do quy doi diem cua khach hang khong duoc phep trong !")
    @Size(min = 1, max = 200, message = "Ly do quy doi diem cua khach hang toi da 200 ki tu !")
    private String reason;
}
