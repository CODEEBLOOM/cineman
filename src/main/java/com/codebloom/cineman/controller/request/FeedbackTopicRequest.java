package com.codebloom.cineman.controller.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FeedbackTopicRequest {

    @NotBlank(message = "Tên chủ đề không được để trống.")
    @Size(max = 100, message = "Title of movie must be less than 100 character !")
    private String topicName;

    @Size(max = 200, message = "Mô tả tối đa 200 ký tự.")
    private String description;
}
