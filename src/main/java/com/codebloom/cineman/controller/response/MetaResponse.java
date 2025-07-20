package com.codebloom.cineman.controller.response;


import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor

public class MetaResponse {

    private Integer currentPage;
    private Integer pageSize;
    private Integer totalPages;
    private Integer totalElements;

}
