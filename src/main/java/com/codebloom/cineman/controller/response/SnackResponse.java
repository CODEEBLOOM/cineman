package com.codebloom.cineman.controller.response;

import com.codebloom.cineman.model.SnackTypeEntity;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class SnackResponse implements Serializable {
    private Long SnackId;
    private String description;
    private String image;
    private String name;
    private Float unit_price;
    private SnackTypeEntity snack_type_id;
    private Boolean is_active;






}
