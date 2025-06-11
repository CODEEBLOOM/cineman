package com.codebloom.cineman.controller.request;


import com.codebloom.cineman.common.enums.MovieStatus;
import com.codebloom.cineman.common.enums.Rating;
import com.codebloom.cineman.model.MovieStatusEntity;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Date;
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class MovieUpdateRequest {
    @NotNull(message = "Trạng thái không được để trống")
    private String status;

    @NotBlank(message = "Tiêu đề không được để trống")
    @Size(max = 100, message = "Tiêu đề không được vượt quá 100 ký tự")
    private String title;

    @NotBlank(message = "Tóm tắt không được để trống")
    @Size(max = 250, message = "Tóm tắt không được vượt quá 250 ký tự")
    private String synopsis;

    @NotBlank(message = "Mô tả chi tiết không được để trống")
    private String detailDescription;

    @NotNull(message = "Ngày phát hành không được để trống")
    @FutureOrPresent(message = "Ngày phát hành phải là hôm nay hoặc tương lai")
    private Date releaseDate;

    @NotBlank(message = "Ngôn ngữ không được để trống")
    private String language;

    @NotNull(message = "Thời lượng không được để trống")
    @Min(value = 1, message = "Thời lượng phải lớn hơn 0 phút")
    private Integer duration;


    private Rating rating;

    @NotNull(message = "Độ tuổi không được để trống")
    @Min(value = 0, message = "Độ tuổi không được âm")
    private Integer age;

    @NotBlank(message = "Link trailer không được để trống")
    @Pattern(regexp = "^(http|https)://.*$", message = "Trailer phải là URL hợp lệ")
    private String trailerLink;

    @NotBlank(message = "Ảnh poster không được để trống")
    @Pattern(regexp = "^(http|https)://.*$", message = "Poster phải là URL hợp lệ")
    private String posterImage;

    @NotBlank(message = "Ảnh banner không được để trống")
    @Pattern(regexp = "^(http|https)://.*$", message = "Banner phải là URL hợp lệ")
    private String bannerImage;

    private Date createdAt;
    private Date updatedAt;
}
