package com.codebloom.cineman.service.mail;

import com.google.zxing.WriterException;
import lombok.Builder;
import lombok.Data;

import java.awt.image.BufferedImage;
import java.io.IOException;

public interface MailService {
    void send(MailModel mailModel);

    default void send(String to, String subject, String body) {
        MailModel mail = MailModel
                .builder()
                .to(to)
                .subject(subject)
                .body(body)
                .build();
        this.send(mail);
    }

    void sendInvoiceWithQRCode(String to, String subject, String body, String qrCode) throws IOException, WriterException;

    @Builder
    @Data
    class MailModel {
        String from;
        String to;
        String cc;
        String bcc;
        String subject;
        String body;
        String filenames;
        BufferedImage qrCodeImage;
    }
}
