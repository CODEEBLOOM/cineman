package com.codebloom.cineman.service.mail;

import com.codebloom.cineman.service.QRCodeService;
import com.google.zxing.WriterException;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.util.ByteArrayDataSource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j(topic = "JAVA_MAIL_SENDER")
@RequiredArgsConstructor
public class MailServiceImpl implements MailService {

    @Value("${app.mail.from-address:${spring.mail.username:no-reply@localhost}}")
    private String fromAddress;

    @Value("${app.mail.from-name:Poly Cinemas}")
    private String fromName;

    private final JavaMailSender mailSender;
    private final QRCodeService qrCodeService;

    List<MailModel> queue = new ArrayList<>();

    @Override
    public void send(MailModel mailModel) {
        queue.add(mailModel);
    }

    @Override
    public void sendInvoiceWithQRCode(String to, String subject, String body, String qrCode) throws IOException, WriterException {
        BufferedImage qrCodeImage = qrCodeService.generateQRCode(qrCode);
        MailModel mail = MailModel
                .builder()
                .to(to)
                .subject(subject)
                .body(body)
                .qrCodeImage(qrCodeImage)
                .build();
        this.send(mail);
    }

    @Scheduled(fixedDelay = 2000)
    public void run() {
        while (!queue.isEmpty()) {
            MailModel mail = queue.remove(0);
            try {
                sendMail(mail);
                log.info("Send mail success to {}", mail.getTo());
            } catch (Exception e) {
                log.info("Send mail error", e);
                log.error("Error message: {}", e.getMessage());
            }
        }
    }

    private void sendMail(MailModel mail) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "utf-8");

            String from = isNullOrEmpty(mail.getFrom()) ? buildDefaultFrom() : mail.getFrom();
            helper.setFrom(from);
            helper.setReplyTo(from);
            helper.setTo(mail.getTo());

            if (!this.isNullOrEmpty(mail.getCc())) {
                helper.setCc(mail.getCc());
            }

            if (!this.isNullOrEmpty(mail.getBcc())) {
                helper.setBcc(mail.getBcc());
            }

            helper.setSubject(mail.getSubject());
            helper.setText(mail.getBody(), true);

            String filenames = mail.getFilenames();
            if (!this.isNullOrEmpty(filenames)) {
                for (String filename : filenames.split("[,;]+")) {
                    File file = new File(filename.trim());
                    helper.addAttachment(file.getName(), file);
                }
            }

            if (mail.getQrCodeImage() != null) {
                ByteArrayOutputStream os = new ByteArrayOutputStream();
                ImageIO.write(mail.getQrCodeImage(), "png", os);
                ByteArrayDataSource dataSource = new ByteArrayDataSource(os.toByteArray(), "image/png");
                helper.addInline("qrImage", dataSource);
            }

            mailSender.send(message);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private boolean isNullOrEmpty(String text) {
        return text == null || text.trim().isEmpty();
    }

    private String buildDefaultFrom() {
        return String.format("%s <%s>", fromName, fromAddress);
    }
}
