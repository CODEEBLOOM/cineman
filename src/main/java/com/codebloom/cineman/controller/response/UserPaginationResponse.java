package com.codebloom.cineman.controller.response;

import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserPaginationResponse {
    private List<UserResponse> userResponses;
    private MetaResponse meta;
}
