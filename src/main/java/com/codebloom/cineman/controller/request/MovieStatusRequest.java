package com.codebloom.cineman.controller.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MovieStatusRequest {

    @NotBlank(message = "Id of movie status is not blank !")
    @Size(max = 25, message = "Id of movie status must be less than 16 character !")
    private String id;
    @NotBlank(message = "Name of movie status is not blank !")
    private String name;
    @NotBlank(message = "Name of movie status is not blank !")
    private String description;
    
}
