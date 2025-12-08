package vn.codegym.lunchbot_be.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.time.Year;
import java.util.logging.Level;
import java.util.logging.Logger;


@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    private final ResourceLoader resourceLoader;

    private static final Logger LOGGER = Logger.getLogger(EmailService.class.getName());

    // ----------------------------------------------------------------------
    // PHƯƠNG THỨC GỬI EMAIL HTML (SỬ DỤNG MIME MESSAGE)
    // ----------------------------------------------------------------------
    @Async
    public void sendRegistrationSuccessEmail(String to, String fullName, String restaurantName, String loginUrl, boolean isMerchant) {
        MimeMessage mimeMessage = mailSender.createMimeMessage();

        try {
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            helper.setTo(to);

            // 1. Đặt Subject dựa trên vai trò
            String subject = isMerchant
                    ? "🎉 Đăng Ký Merchant Thành Công trên LunchBot"
                    : "👋 Chào Mừng Đến Với LunchBot!";
            helper.setSubject(subject);

            // 2. CHỌN TEMPLATE PHÙ HỢP
            String templatePath = isMerchant
                    ? "classpath:templates/emails/merchant_registration_template.html"
                    : "classpath:templates/emails/user_registration_template.html"; // Template mới

            String htmlContent = buildHtmlContent(
                    templatePath,
                    to,
                    fullName,
                    restaurantName,
                    loginUrl
            );

            helper.setText(htmlContent, true);

            mailSender.send(mimeMessage);
            LOGGER.log(Level.INFO, "Gửi email thành công tới: {0}", to);

        } catch (MailException | MessagingException exception) {
            LOGGER.log(Level.SEVERE, "Lỗi khi gửi email HTML tới: " + to, exception);
            throw new RuntimeException("Không thể gửi email thông báo HTML.", exception);
        }
    }

    // ----------------------------------------------------------------------
    // HÀM XÂY DỰNG NỘI DUNG HTML
    // ----------------------------------------------------------------------
    private String buildHtmlContent(String templatePath, String email, String fullName, String restaurantName, String loginUrl) {
        String template = readTemplateFile(templatePath); // Giờ đã sử dụng templatePath

        String safeFullName = fullName != null ? fullName : email;
        String safeRestaurantName = restaurantName != null ? restaurantName : "";

        // Thay thế các biến động
        return template
                .replace("${fullName}", safeFullName)
                .replace("${restaurantName}", safeRestaurantName)
                .replace("${email}", email)
                .replace("${loginUrl}", loginUrl)
                .replace("${currentYear}", String.valueOf(Year.now().getValue()));
    }

    // ----------------------------------------------------------------------
    // HÀM ĐỌC FILE TEMPLATE
    // ----------------------------------------------------------------------
    private String readTemplateFile(String filePath) {
        try {
            Resource resource = resourceLoader.getResource(filePath);

            try (Reader reader = new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8)) {
                return FileCopyUtils.copyToString(reader);
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Không thể đọc file template: " + filePath, e);
            return "<h1>Lỗi: Không tìm thấy template email.</h1>";
        }
    }
}