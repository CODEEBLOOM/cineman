package com.codebloom.cineman.controller.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserRoleResponse {
    private Long id;
    private String name;
    private String description;
    private Long userId;
    private Long roleId;
    private String roleName;
}

