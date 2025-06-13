package com.codebloom.cineman.controller.request;

import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.validation.constraints.*;
import lombok.*;

import java.util.Date;
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class MovieCreationRequest {

    @NotBlank(message = "Title's movie is not blank !")
    @Size(max = 100, message = "Title of movie must be less than 100 character !")
    private String title;

    @NotBlank(message = "Synopsis's movie is not blank !")
    @Size(max = 250, message = "Synopsis's movie must be less than 250 character !")
    private String synopsis;

    @NotBlank(message = "Detail description of movie is not blank !")
    private String detailDescription;

    @NotNull(message = "Release date of movie is not null !")
    @Temporal(TemporalType.DATE)
    private Date releaseDate;

    @NotBlank(message = "Language's movie is not blank !")
    private String language;

    @NotNull(message = "")
    @Min(value = 1, message = "Duration's movie is must be greater than 1 !")
    private Integer duration;

    @NotNull(message = "Age limit of movie is not null !")
    @Min(value = 0, message = "Age limit of movie must be in rage [0 - 100] !")
    @Max(value = 100, message = "Age limit of movie must be in rage [0 - 100] !")
    private Integer age;

    @NotBlank(message = "Trailer link of movie is not blank!")
    @Pattern(regexp = "^(https?|ftp)://.*$", message = "Trailer link of movie incorrect format!")
    private String trailerLink;

    @NotBlank(message = "Poster's movie is not blank!")
    private String posterImage;

    @NotBlank(message = "Banner's movie is not blank!")
    private String bannerImage;

}
