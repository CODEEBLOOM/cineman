package com.codebloom.cineman.controller.util;


import com.codebloom.cineman.service.mail.MailService;
import com.codebloom.cineman.service.util.EmailService;
import com.google.zxing.WriterException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@Slf4j(topic = "EMAIL-CONTROLLER")
public class MailController {

    private final EmailService emailService;
    private final MailService mailService;

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

    @PostMapping("/mail/send")
    public void sendProcess() throws IOException, WriterException {
        String body = """
                <html>
                  <body style="font-family: sans-serif; background: #f7f7f7; padding: 20px">
                    <div
                      style="
                        max-width: 600px;
                        margin: auto;
                        background: #fff;
                        padding: 20px;
                        border: 1px solid #ddd;
                      "
                    >
                      <div style="text-align: center">
                        <img
                          src="cid:qrImage"
                          alt="QR Code"
                          width="150"
                          height="150"
                        />
                        <p style="color: #f4499f; font-style: italic">
                          Mã QR Code này dùng để quét và nhận vé của bạn tại rạp chiếu Cineman
                          Cinemas
                        </p>
                      </div>
                
                      <hr />
                      <h3 style="color: #2e4ca6">Thông tin vé</h3>
                      <table width="100%" cellspacing="0" cellpadding="5">
                        <tr>
                          <td>Mã vé:</td>
                          <td align="right">123332223</td>
                        </tr>
                        <tr>
                          <td>Tên phim:</td>
                          <td align="right">Nhà Bà Nữ</td>
                        </tr>
                        <tr>
                          <td>Rạp chiếu:</td>
                          <td align="right">Cineman Quang Trung</td>
                        </tr>
                        <tr>
                          <td>Phòng chiếu:</td>
                          <td align="right">P01</td>
                        </tr>
                        <tr>
                          <td>Xuất chiếu:</td>
                          <td align="right">22/07/2025 - 18:00</td>
                        </tr>
                        <tr>
                          <td>Ghế:</td>
                          <td align="right">A1, A2, A3, A4</td>
                        </tr>
                      </table>
                
                      <hr />
                      <h3 style="color: #2e4ca6">Thông tin combo</h3>
                      <table width="100%" cellspacing="0" cellpadding="5">
                        <tr>
                          <td>Combo premium:</td>
                          <td align="right">2 x 100.000đ</td>
                        </tr>
                      </table>
                
                      <hr />
                      <h3 style="color: #2e4ca6">Thông tin khuyến mãi</h3>
                      <table width="100%" cellspacing="0" cellpadding="5">
                        <tr>
                          <td>Giảm giá:</td>
                          <td align="right">50.000đ</td>
                        </tr>
                        <tr>
                          <td>Điểm cineman:</td>
                          <td align="right">10.000</td>
                        </tr>
                      </table>
                      <hr />
                      <h3 style="text-align: right">Tổng cộng: 100.000đ</h3>
                    </div>
                  </body>
                </html>
                """;
        mailService.sendInvoiceWithQRCode(
                "sondqps41027@gmail.com",
                "Test Mail",
                body,"fdfdd");
    }

}
