package com.codebloom.cineman.controller.request;

import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
public class PageRequest {

    private int page = 0;
    private int size = 20;
    private Map<String, String> queries = new HashMap<>();

}
