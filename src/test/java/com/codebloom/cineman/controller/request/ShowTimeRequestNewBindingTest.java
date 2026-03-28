package com.codebloom.cineman.controller.request;

import com.codebloom.cineman.common.enums.ShowTimeStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.format.support.DefaultFormattingConversionService;
import org.springframework.validation.DataBinder;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class ShowTimeRequestNewBindingTest {

    @Test
    void bind_shouldParseShowDateFromIsoQueryParam() {
        ShowTimeRequestNew request = new ShowTimeRequestNew();
        DataBinder binder = new DataBinder(request);
        binder.setConversionService(new DefaultFormattingConversionService());

        binder.bind(new MutablePropertyValues(Map.of(
                "movieTheaterId", "1",
                "showDate", "2026-03-14",
                "showTimeStatus", "VALID"
        )));

        assertFalse(binder.getBindingResult().hasErrors());
        assertEquals(1L, request.getMovieTheaterId());
        assertEquals(ShowTimeStatus.VALID, request.getShowTimeStatus());
        assertEquals(
                LocalDate.of(2026, 3, 14),
                request.getShowDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
        );
    }
}
