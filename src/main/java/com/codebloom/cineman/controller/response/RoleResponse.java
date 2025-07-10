package com.codebloom.cineman.controller.response;


import lombok.*;

import java.util.Set;

@Getter
@Setter
@Builder
public class RoleResponse {
    private String roleId;
    private String name;
    private Set<String> permissions;
}
