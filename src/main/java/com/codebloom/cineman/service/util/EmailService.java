package com.codebloom.cineman.service.util;

import java.io.IOException;

public interface EmailService {

    void send(String to, String subject, String text);
    void emailVerification(String to,String phoneNumber,  String name) throws IOException;
}
