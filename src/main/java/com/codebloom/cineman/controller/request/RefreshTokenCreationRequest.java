 package com.codebloom.cineman.controller.request;

 import lombok.Getter;

 @Getter
public class RefreshTokenCreationRequest {
     private String token;
     private long userId;
}
