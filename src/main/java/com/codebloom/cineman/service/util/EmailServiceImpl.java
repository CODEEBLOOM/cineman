package com.codebloom.cineman.service.util;

import com.codebloom.cineman.service.JwtService;
import com.codebloom.cineman.service.mail.MailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Service
@RequiredArgsConstructor
@Slf4j(topic = "EMAIL-SERVICE")
public class EmailServiceImpl implements EmailService {

    @Value("${app.mail.verification-link:http://localhost:${server.port}${api.path}/auth/confirm-email}")
    private String verificationLink;

    private final MailService mailService;
    private final JwtService jwtService;

    @Override
    public void send(String to, String subject, String text) {
        mailService.send(to, subject, text);
        log.info("Email queued successfully to {}", to);
    }

    @Override
    public void emailVerification(String to, String phoneNumber, String name) throws IOException {
        log.info("Email verification started with email to: {}", to);

        String subject = "Xac thuc tai khoan";
        String tokenVerify = jwtService.generateTokenToVerify(phoneNumber, to);
        String verifyUrl = buildVerificationUrl(tokenVerify);
        String html = buildVerificationHtml(name, verifyUrl);

        mailService.send(to, subject, html);
        log.info("Verification email queued successfully to {}", to);
    }

    private String buildVerificationUrl(String tokenVerify) {
        String delimiter = verificationLink.contains("?") ? "&" : "?";
        return verificationLink + delimiter + "secretCode="
                + URLEncoder.encode(tokenVerify, StandardCharsets.UTF_8);
    }

    private String buildVerificationHtml(String name, String verifyUrl) {
        String safeName = name == null || name.isBlank() ? "ban" : name;
        return """
                <html>
                <body style="font-family:Arial,sans-serif;color:#1f2937;line-height:1.6;">
                    <h2 style="margin-bottom:16px;">Xac thuc tai khoan Poly Cinemas</h2>
                    <p>Xin chao %s,</p>
                    <p>Cam on ban da dang ky tai khoan. Vui long xac thuc email de kich hoat tai khoan.</p>
                    <p style="margin:24px 0;">
                        <a href="%s"
                           style="background:#b91c1c;color:#ffffff;padding:12px 20px;text-decoration:none;border-radius:6px;display:inline-block;">
                            Xac thuc email
                        </a>
                    </p>
                    <p>Neu khong bam duoc nut, hay sao chep lien ket sau vao trinh duyet:</p>
                    <p><a href="%s">%s</a></p>
                </body>
                </html>
                """.formatted(safeName, verifyUrl, verifyUrl, verifyUrl);
    }
}
