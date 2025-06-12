 package com.codebloom.cineman.controller.request;

 import lombok.Getter;

 @Getter
public class RefreshTokenUpdateRequest {
     private String token;
     private long userId;
}
