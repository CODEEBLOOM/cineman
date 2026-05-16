package com.codebloom.cineman.controller.response;


import lombok.*;

import java.util.Set;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoleResponse {
    private String roleId;
    private String name;
    private Boolean status;
    private Set<Integer> permissionIds;
    private Set<String> permissions;
}
