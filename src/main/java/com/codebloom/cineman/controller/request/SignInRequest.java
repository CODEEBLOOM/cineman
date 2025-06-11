package com.codebloom.cineman.controller.request;

import lombok.Getter;

import java.io.Serializable;

@Getter
public class SignInRequest implements Serializable {
    private String email;
    private String password;
}
