package com.codebloom.cineman.controller.request;

import com.codebloom.cineman.common.enums.Method;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PermissionRequest {


    @NotBlank(message = "Title không được để trống")
    @Size(max = 100, message = "Title không được vượt quá 100 ký tự")
    @NotNull( message = "Title không được phép null !")
    String title;

    @Size(max = 500, message = "Description không được vượt quá 500 ký tự")
    String description;

    @NotNull(message = "Method không được để trống")
    Method method;

    @NotBlank(message = "URL không được để trống")
    @Size(max = 200, message = "URL không được vượt quá 200 ký tự")
    @NotNull( message = "URL không được phép null !")
    String url;
}
