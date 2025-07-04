package com.codebloom.cineman.controller.response;


import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class MetaResponse {

    private Integer currentPage;
    private Integer pageSize;
    private Integer totalPages;
    private Integer totalElements;

}
