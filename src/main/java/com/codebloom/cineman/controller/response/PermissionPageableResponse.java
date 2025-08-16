package com.codebloom.cineman.controller.response;

import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PermissionPageableResponse {
    List<PermissionResponse> permissionResponses;
    private MetaResponse meta;

}
