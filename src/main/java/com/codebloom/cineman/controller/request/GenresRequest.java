package com.codebloom.cineman.controller.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GenresRequest {

    @NotBlank(message = "Name's genre is not blank !")
    @Size(max = 100, message = "Name's genre is must be less than or equal 100 character !")
    private String name;

    @NotBlank(message = "Description's genre is not blank !")
    @Size(max = 250, message = "Description's genre is must be less than or equal 250 character !")
    private String description;

}
