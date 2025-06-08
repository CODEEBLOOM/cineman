package com.codebloom.cineman.controller.request;

import com.codebloom.cineman.common.enums.MovieStatus;
import com.codebloom.cineman.common.enums.Rating;
import com.codebloom.cineman.model.MovieStatusEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.util.Date;
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class MovieCreationRequest {

    @NotNull(message = "Trạng thái không được để trống")
    private MovieStatus status;

    @NotBlank(message = "Tiêu đề không được để trống")
    @Size(max = 100, message = "Tiêu đề không được vượt quá 100 ký tự")
    private String title;

    @NotBlank(message = "Tóm tắt không được để trống")
    @Size(max = 250, message = "Tóm tắt không được vượt quá 250 ký tự")
    private String synopsis;

    @NotBlank(message = "Mô tả chi tiết không được để trống")
    private String detailDescription;

    @NotNull(message = "Ngày phát hành không được để trống")
    @FutureOrPresent(message = "Ngày phát hành phải là hiện tại hoặc tương lai")
    private Date releaseDate;

    @NotBlank(message = "Ngôn ngữ không được để trống")
    private String language;

    @NotNull(message = "Thời lượng không được để trống")
    @Min(value = 1, message = "Thời lượng phải lớn hơn 0")
    private Integer duration;

    @NotNull(message = "Đánh giá không được để trống")
    private Rating rating;

    @NotNull(message = "Độ tuổi không được để trống")
    @Min(value = 0, message = "Độ tuổi không được âm")
    private Integer age;

    @NotBlank(message = "Link trailer không được để trống")
    @Pattern(regexp = "^(https?|ftp)://.*$", message = "Trailer phải là URL hợp lệ")
    private String trailerLink;

    @NotBlank(message = "Ảnh poster không được để trống")
    @Pattern(regexp = "^(https?|ftp)://.*$", message = "Poster phải là URL hợp lệ") //  ^(https?|ftp)://.*$ là 1 biểu thức chính quy học từ java 1
    private String posterImage;

    @NotBlank(message = "Ảnh banner không được để trống")
    @Pattern(regexp = "^(https?|ftp)://.*$", message = "Banner phải là URL hợp lệ")
    private String bannerImage;


    private Date createdAt;
    private Date updatedAt;
}
