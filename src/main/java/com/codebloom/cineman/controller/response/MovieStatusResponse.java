package com.codebloom.cineman.controller.response;

import jakarta.persistence.Column;
import jakarta.persistence.Id;
import lombok.Getter;

@Getter
public class MovieStatusResponse {
    private String statusId;
    private String name;
    private String description;
}
