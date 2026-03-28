package com.codebloom.cineman.controller.request;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class MovieRequestValidationTest {

    private final Validator validator;

    MovieRequestValidationTest() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        this.validator = factory.getValidator();
    }

    @Test
    void createRequest_shouldRequireDirectorsAndCasts() {
        MovieCreationRequest request = new MovieCreationRequest(
                "Movie",
                "Synopsis",
                "Detail",
                new Date(),
                new Date(),
                "VN",
                120,
                16,
                "SC",
                List.of(1),
                null,
                null,
                "https://example.com/trailer",
                "poster.jpg",
                "banner.jpg"
        );

        Set<ConstraintViolation<MovieCreationRequest>> violations = validator.validate(request);

        assertThat(violations)
                .extracting(violation -> violation.getPropertyPath().toString())
                .contains("directors", "casts");
    }

    @Test
    void updateRequest_shouldRequireNonEmptyDirectorsAndCasts() {
        MovieUpdateRequest request = new MovieUpdateRequest(
                1,
                "Movie",
                "Synopsis",
                "Detail",
                new Date(),
                new Date(),
                "VN",
                120,
                16,
                "SC",
                List.of(1),
                List.of(),
                List.of(),
                "https://example.com/trailer",
                "poster.jpg",
                "banner.jpg"
        );

        Set<ConstraintViolation<MovieUpdateRequest>> violations = validator.validate(request);

        assertThat(violations)
                .extracting(violation -> violation.getPropertyPath().toString())
                .contains("directors", "casts");
    }
}
