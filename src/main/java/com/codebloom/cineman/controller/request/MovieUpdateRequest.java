package com.codebloom.cineman.controller.request;


import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class MovieUpdateRequest {

    @NotNull (message = "Id phim không được để trống!")
    @Min(value = 1, message = "Id phim phải lớn hơn 0!")
    private Integer movieId;

    @NotBlank(message = "Tiêu đề phim không được để trống!")
    @Size(max = 100, message = "Tiêu đề phim phải nhỏ hơn 100 ký tự!")
    private String title;

    @NotBlank(message = "Tóm tắt phim không được để trống!")
    @Size(max = 250, message = "Tóm tắt phim phải nhỏ hơn 250 ký tự!")
    private String synopsis;

    @NotBlank(message = "Mô tả chi tiết phim không được để trống!")
    private String detailDescription;

    @NotNull(message = "Ngày khởi chiếu không được để trống!")
    private Date releaseDate;

    @NotNull(message = "Ngày kết thúc không được để trống!")
    private Date endDate;

    @NotBlank(message = "Ngôn ngữ phim không được để trống!")
    private String language;

    @NotNull(message = "Thời lượng phim không được để trống!")
    @Min(value = 1, message = "Thời lượng phim phải lớn hơn 1 phút!")
    private Integer duration;

    @NotNull(message = "Giới hạn độ tuổi không được để trống!")
    @Min(value = 0, message = "Độ tuổi phải nằm trong khoảng từ 0 đến 100!")
    @Max(value = 100, message = "Độ tuổi phải nằm trong khoảng từ 0 đến 100!")
    private Integer age;

    @NotNull(message = "Trạng thái phim không được để trống!")
    @Pattern(regexp = "SC|DC|NC|DB|CNS", message = "Trạng thái phim không hợp lệ!")
    private String status;

    @NotNull(message = "Thể loại phim không được để trống!")
    private List<Integer> genres;

    @NotEmpty(message = "Directors list is required")
    private List<
            @NotNull(message = "Director id must not be null")
            @Min(value = 1, message = "Director id must be greater than 0")
            Integer> directors;

    @NotEmpty(message = "Casts list is required")
    private List<
            @NotNull(message = "Cast id must not be null")
            @Min(value = 1, message = "Cast id must be greater than 0")
            Integer> casts;

    @NotBlank(message = "Link trailer không được để trống!")
    @Pattern(regexp = "^(https?|ftp)://.*$", message = "Định dạng link trailer không hợp lệ!")
    private String trailerLink;

    @NotBlank(message = "Ảnh poster không được để trống!")
    private String posterImage;

    @NotBlank(message = "Ảnh banner không được để trống!")
    private String bannerImage;

}
