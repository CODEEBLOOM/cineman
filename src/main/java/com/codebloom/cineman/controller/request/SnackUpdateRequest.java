package com.codebloom.cineman.controller.request;

import com.codebloom.cineman.common.enums.GenderUser;
import com.codebloom.cineman.common.enums.UserType;
import com.codebloom.cineman.model.SnackTypeEntity;
import lombok.Getter;

import java.util.Date;

@Getter
public class SnackUpdateRequest {

    private Integer SnackId;
    private String description;
    private String image;
    private String name;
    private Float unit_price;
    private SnackTypeEntity snack_type_id;

    // Boolean IsActive


}
