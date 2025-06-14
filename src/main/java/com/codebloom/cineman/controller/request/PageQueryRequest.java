package com.codebloom.cineman.controller.request;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PageQueryRequest {
    private Integer page = 0;
    private Integer size = 20;
    private String query;
}
