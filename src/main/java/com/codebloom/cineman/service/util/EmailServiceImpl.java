package com.codebloom.cineman.service.util;

import com.codebloom.cineman.common.enums.TokenType;
import com.codebloom.cineman.service.JwtService;
import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import com.sendgrid.helpers.mail.objects.Personalization;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j(topic = "EMAIL-SERVICE")
public class EmailServiceImpl implements EmailService {



    @Value("${spring.sendgrid.from-email}")
    private String from;

    @Value("${spring.sendgrid.template-id}")
    private String templateId;

    @Value("${spring.sendgrid.verification-link}")
    private String verificationLink;


    private final SendGrid sendGrid;
    private final JwtService jwtService;

    /**
     * Send email by send grid
     *
     * @param to:      send email to someone
     * @param subject:
     * @param text:
     */
    @Override
    public void send(String to, String subject, String text) {
        Email fromEmail = new Email(from);
        Email toEmail = new Email(to);

        Content content = new Content("text/plain", text);
        Mail mail = new Mail(fromEmail, subject, toEmail, content);

        Request request = new Request();
        try {
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());

            Response response = sendGrid.api(request);

            // ACCEPTED //
            if (response.getStatusCode() == 202) {
                log.info("Email sent successfully");
            } else {
                log.info("Email sent failed");
            }
        } catch (IOException e) {
            log.error("Error occurred while sending email, error: ", e.getMessage());
        }

    }

    /**
     * Email verification by sendgrid
     *
     * @param to:
     * @param name:
     */
    @Override
    public void emailVerification(String to, String phoneNumber, String name) throws IOException {
        log.info("Email verification started with email to: " + to);

        Email fromEmail = new Email(from, "Phòng dịch vụ Cineman");
        Email toEmail = new Email(to);

        // Tạo token verify có thời hạn 10 phút //
        String subject = "Xác thực tài khoản";
        String tokenVerify = jwtService.generateTokenToVerify(phoneNumber, to);
        String secretCode = String.format("?secretCode=%s", tokenVerify);


        Map<String, String> map = new HashMap<>();
        map.put("name", name);
        map.put("verification_link", verificationLink + secretCode);
        map.put("subject", subject);

        Mail mail = new Mail();
        mail.setFrom(fromEmail);
        mail.setSubject(subject);

        Personalization personalization = new Personalization();
        personalization.addTo(toEmail);
        map.forEach(personalization::addDynamicTemplateData);
        mail.addPersonalization(personalization);
        mail.setTemplateId(templateId);

        Request request = new Request();
        request.setMethod(Method.POST);
        request.setEndpoint("mail/send");
        request.setBody(mail.build());

        Response response = sendGrid.api(request);

        // ACCEPTED //
        if (response.getStatusCode() == 202) {
            log.info("Verification sent successfully");
        } else {
            log.error("Verification sent failed");
        }


    }
}
