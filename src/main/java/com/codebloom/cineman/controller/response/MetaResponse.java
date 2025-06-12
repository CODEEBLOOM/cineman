package com.codebloom.cineman.controller.response;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MetaResponse {

    private Integer currentPage;
    private Integer pageSize;
    private Integer totalPages;
    private Integer totalElements;

}
