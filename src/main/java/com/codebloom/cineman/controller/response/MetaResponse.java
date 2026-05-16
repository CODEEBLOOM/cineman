package com.codebloom.cineman.controller.response;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MetaResponse {

    private Integer currentPage;
    private Integer pageSize;
    private Integer totalPages;
    private Integer totalElements;

}
