package com.codebloom.cineman.controller.request;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class PageQueryRequest {

    @Builder.Default
    private int page = 0;
    @Builder.Default
    private int size = 20;
    private String query;

}
