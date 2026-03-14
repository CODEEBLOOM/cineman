package com.codebloom.cineman.controller.request;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
public class UpdateMulDetailBookingSnack {

    @Builder.Default
    private List<Long> idsDelete = new ArrayList<>();
    private List<DetailBookingSnackRequest> data;

}
