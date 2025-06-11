package com.codebloom.cineman.controller;


import com.codebloom.cineman.controller.request.SignInRequest;
import com.codebloom.cineman.controller.response.TokenResponse;
import com.codebloom.cineman.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class LoginController {

    private final JwtService jwtService;

//    @PostMapping("/login")
//    public ResponseEntity<?> login(@RequestBody SignInRequest request) {
//        // Đây là mô phỏng. Thực tế nên check DB hoặc service
//        if ("user".equals(request.getPhoneNumber()) && "123".equals(request.getPassword())) {
//            String token = jwtService.generateAccessToken( request.getPhoneNumber());
//            return ResponseEntity.ok(new TokenResponse(token));
//        } else {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
//        }
//    }


}
