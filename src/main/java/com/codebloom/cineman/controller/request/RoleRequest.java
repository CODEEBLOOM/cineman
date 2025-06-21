package com.codebloom.cineman.controller.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class RoleRequest {
    @NotBlank
    @Size(max = 25)
    String roleId;

    @NotBlank
    @Size(max = 100)
    String name;

    Set<Integer> permissionIds;
}
