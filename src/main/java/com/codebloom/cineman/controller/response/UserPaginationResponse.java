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
    private Meta meta;

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class Meta {
        private Integer currentPage;
        private Integer pageSize;
        private Integer totalPages;
        private Integer totalElements;
    }
}
