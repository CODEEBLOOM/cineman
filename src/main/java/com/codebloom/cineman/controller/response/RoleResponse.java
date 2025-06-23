package com.codebloom.cineman.controller.response;


import lombok.*;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoleResponse {
    String roleId;
    String name;
    Set<String> permissions;
}
