package com.codebloom.cineman.controller.response;


import lombok.*;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserRolePageableResponse {
    private List<UserRoleResponse> userRoleResponses;
    private MetaResponse meta;
}

