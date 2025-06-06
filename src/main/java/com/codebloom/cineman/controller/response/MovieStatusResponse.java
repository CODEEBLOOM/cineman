package com.codebloom.cineman.controller.response;

import jakarta.persistence.Column;
import jakarta.persistence.Id;
import lombok.Getter;

@Getter
public class MovieStatusResponse {
    private Integer statusId;
    private String name;
    private String description;
}
