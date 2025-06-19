package com.codebloom.cineman.controller.util;


import com.codebloom.cineman.service.util.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@Slf4j(topic = "EMAIL-CONTROLLER")
public class MailController {

    private final EmailService emailService;

    @GetMapping("/send-email")
    public void sendEmail(@RequestParam String to,@RequestParam String subject,@RequestParam String content) {
        log.info("Sending email to " + to);
        emailService.send(to, subject, content);
        log.info("Email sent");
    }

    @GetMapping("/verification-email")
    public void emailVerification(@RequestParam String to,@RequestParam String phoneNumber, @RequestParam String name) throws IOException {
        log.info("Sending email to " + to);
        emailService.emailVerification(to,phoneNumber, name);
        log.info("Email sent");
    }

}
