package com.codebloom.cineman.message;

import com.codebloom.cineman.controller.response.PromotionResponse;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PromotionActivatedMessage {

    private String type;
    private PromotionResponse promotion;
}
