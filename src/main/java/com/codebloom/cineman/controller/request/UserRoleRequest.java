package com.codebloom.cineman.controller.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserRoleRequest {
    private String name;
    private String description;

    @NotNull(message = "UserId can't by null")
    @Min(value = 1)
    private Long userId;

    @NotNull(message = "roleId can't by null")
    private String roleId;
}

