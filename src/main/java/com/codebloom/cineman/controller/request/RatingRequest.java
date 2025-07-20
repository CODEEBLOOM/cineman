package com.codebloom.cineman.controller.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class RatingRequest {
	@NotNull
    private Long ticketId;
	
    @NotNull(message = "Rating không được để trống")
    @Min(value = 1, message = "Rating thấp nhất là 1")
    @Max(value = 5, message = "Rating cao nhất là 5")
    private Integer rating;
}
