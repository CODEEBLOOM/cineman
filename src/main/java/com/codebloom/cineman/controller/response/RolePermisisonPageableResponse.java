package com.codebloom.cineman.controller.response;

import lombok.*;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RolePermisisonPageableResponse {
    List<RolePermissionResponse> rolePermissionResponses;
    private MetaResponse meta;
}
