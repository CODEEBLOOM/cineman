package com.codebloom.cineman.common.utils;

import com.codebloom.cineman.controller.request.ShowTimeRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;

public class ShowTimeValidator implements ConstraintValidator<ShowTimeValidation, ShowTimeRequest> {

    @Override
    public void initialize(ShowTimeValidation constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(ShowTimeRequest request, ConstraintValidatorContext context) {

        if (request == null || request.getShowDate() == null || request.getStartTime() == null) {
            return true; // Để các @NotNull xử lý nếu trường là null
        }

        // Lấy ngày hiện tại
        LocalDate today = LocalDate.now();
        LocalTime now = LocalTime.now();

        LocalDate showDate = request.getShowDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalTime startTime = request.getStartTime();

        // 1. Nếu showDate là một ngày trong tương lai (sau hôm nay)
        if (showDate.isAfter(today)) {
            return true;
        }

        // 2. Nếu showDate là HÔM NAY
        if (showDate.isEqual(today)) {
            // Thì startTime phải là hiện tại hoặc trong tương lai
            if (startTime.isAfter(now) || startTime.equals(now)) {
                return true;
            } else {
                // Nếu startTime trong quá khứ so với thời điểm hiện tại
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate("Thời gian bắt đầu phải là hiện tại hoặc trong tương lai.")
                        .addPropertyNode("startTime") // Gắn lỗi vào trường startTime
                        .addConstraintViolation();
                return false;
            }
        }

        // 3. Nếu showDate là một ngày trong quá khứ
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate("Ngày chiếu không thể là ngày trong quá khứ.")
                .addPropertyNode("showDate")
                .addConstraintViolation();
        return false;
    }
}
