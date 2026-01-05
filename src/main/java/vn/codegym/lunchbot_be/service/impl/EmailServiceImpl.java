package vn.codegym.lunchbot_be.service.impl;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import vn.codegym.lunchbot_be.service.EmailService;

import java.time.LocalDateTime;
import java.time.Year;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailServiceImpl implements EmailService {

    private final TemplateEngine templateEngine;

    private final JavaMailSender mailSender;

    @Value("${app.mail.from:noreply@lunchbot.com}")
    private String fromEmail;

    @Value("${app.mail.support:support@lunchbot.com}")
    private String supportEmail;

    @Value("${app.name:LunchBot}")
    private String appName;

    @Value("${app.url:http://localhost:5173}")
    private String appUrl;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    @Override
    @Async
    public void sendVerificationEmail(String to, String fullName, String token) {
        try {
            Context context = new Context();
            context.setVariable("fullName", fullName != null ? fullName : to);
            context.setVariable("verificationLink", appUrl + "/login?token=" + token);
            context.setVariable("appName", appName);
            context.setVariable("currentYear", Year.now().getValue());

            String htmlContent = templateEngine.process("emails/email-verification", context);

            sendHtmlEmail(to, "✅ Xác thực Email để kích hoạt tài khoản LunchBot", htmlContent);

            log.info("✅ Verification email sent successfully to: {}", to);
        } catch (Exception e) {
            log.error("❌ Failed to send verification email to {}: {}", to, e.getMessage(), e);
            throw new RuntimeException("Không thể gửi email xác thực", e);
        }
    }

    @Override
    @Async
    public void sendRegistrationSuccessEmail(String to, String fullName, String restaurantName, String loginUrl, boolean isMerchant) {
        try {
            Context context = new Context();
            context.setVariable("fullName", fullName != null ? fullName : to);
            context.setVariable("email", to);
            context.setVariable("restaurantName", restaurantName != null ? restaurantName : "");
            context.setVariable("appUrl", loginUrl != null ? loginUrl : appUrl);
            context.setVariable("currentYear", Year.now().getValue());
            context.setVariable("appName", appName);
            context.setVariable("supportEmail", supportEmail);

            String templateName = isMerchant
                    ? "emails/merchant-registration"
                    : "emails/user-registration";

            String subject = isMerchant
                    ? "🎉 Đăng Ký Merchant Thành Công trên LunchBot"
                    : "👋 Chào Mừng Đến Với LunchBot!";

            String htmlContent = templateEngine.process(templateName, context);
            sendHtmlEmail(to, subject, htmlContent);

            log.info("✅ Registration email sent successfully to: {}", to);
        } catch (Exception e) {
            log.error("❌ Failed to send registration email to {}: {}", to, e.getMessage(), e);
            throw new RuntimeException("Không thể gửi email thông báo đăng ký", e);
        }
    }

    @Override
    @Async
    public void sendMerchantApprovalEmail(String merchantEmail, String merchantName, String restaurantName, String reason) {
        try {
            Context context = createMerchantContext(merchantEmail, merchantName, restaurantName, reason);
            context.setVariable("reason", reason != null ? reason : "Hồ sơ đã đạt yêu cầu");

            String htmlContent = templateEngine.process("emails/merchant-approval", context);
            sendHtmlEmail(merchantEmail, "🎉 Chúc mừng! Tài khoản merchant của bạn đã được phê duyệt", htmlContent);

            log.info("✅ Merchant approval email sent to: {}", merchantEmail);
        } catch (Exception e) {
            log.error("❌ Failed to send merchant approval email to {}: {}", merchantEmail, e.getMessage(), e);
        }
    }

    @Override
    @Async
    public void sendMerchantRejectionEmail(String merchantEmail, String merchantName, String restaurantName, String reason) {
        try {
            Context context = createMerchantContext(merchantEmail, merchantName, restaurantName, reason);
            context.setVariable("reason", reason != null ? reason : "Hồ sơ chưa đạt yêu cầu");

            String htmlContent = templateEngine.process("emails/merchant-rejection", context);
            sendHtmlEmail(merchantEmail, "❌ Thông báo về việc xét duyệt tài khoản merchant", htmlContent);

            log.info("✅ Merchant rejection email sent to: {}", merchantEmail);
        } catch (Exception e) {
            log.error("❌ Failed to send merchant rejection email to {}: {}", merchantEmail, e.getMessage(), e);
        }
    }

    @Override
    @Async
    public void sendMerchantLockedEmail(String merchantEmail, String merchantName, String restaurantName, String reason) {
        try {
            Context context = createMerchantContext(merchantEmail, merchantName, restaurantName, reason);
            context.setVariable("reason", reason != null ? reason : "Vi phạm chính sách");

            String htmlContent = templateEngine.process("emails/merchant-locked", context);
            sendHtmlEmail(merchantEmail, "🚫 Thông báo khóa tài khoản merchant", htmlContent);

            log.info("✅ Merchant locked email sent to: {}", merchantEmail);
        } catch (Exception e) {
            log.error("❌ Failed to send merchant locked email to {}: {}", merchantEmail, e.getMessage(), e);
        }
    }

    @Override
    @Async
    public void sendMerchantUnlockedEmail(String merchantEmail, String merchantName, String restaurantName, String reason) {
        try {
            Context context = createMerchantContext(merchantEmail, merchantName, restaurantName, reason);
            context.setVariable("reason", reason != null ? reason : "Tài khoản đã được mở khóa");

            String htmlContent = templateEngine.process("emails/merchant-unlocked", context);
            sendHtmlEmail(merchantEmail, "✅ Thông báo mở khóa tài khoản merchant", htmlContent);

            log.info("✅ Merchant unlocked email sent to: {}", merchantEmail);
        } catch (Exception e) {
            log.error("❌ Failed to send merchant unlocked email to {}: {}", merchantEmail, e.getMessage(), e);
        }
    }

    @Override
    @Async
    public void sendShippingPartnerLockedEmail(String partnerEmail, String partnerName, String reason) {
        try {
            Context context = new Context();
            context.setVariable("partnerName", partnerName);
            context.setVariable("reason", reason != null ? reason : "Vi phạm chính sách dịch vụ");
            context.setVariable("appName", appName);
            context.setVariable("supportEmail", supportEmail);
            context.setVariable("currentDate", getCurrentDate());

            String htmlContent = templateEngine.process("emails/shipping-partner-locked", context);
            sendHtmlEmail(partnerEmail, "🚫 Thông báo khóa tài khoản đối tác vận chuyển", htmlContent);

            log.info("✅ Shipping partner locked email sent to: {}", partnerEmail);
        } catch (Exception e) {
            log.error("❌ Failed to send shipping partner locked email to {}: {}", partnerEmail, e.getMessage(), e);
        }
    }

    @Override
    @Async
    public void sendShippingPartnerUnlockedEmail(String partnerEmail, String partnerName, String reason) {
        try {
            Context context = new Context();
            context.setVariable("partnerName", partnerName);
            context.setVariable("reason", reason != null ? reason : "Tài khoản đã được mở khóa");
            context.setVariable("appName", appName);
            context.setVariable("appUrl", appUrl);
            context.setVariable("supportEmail", supportEmail);
            context.setVariable("currentDate", getCurrentDate());

            String htmlContent = templateEngine.process("emails/shipping-partner-unlocked", context);
            sendHtmlEmail(partnerEmail, "✅ Thông báo mở khóa tài khoản đối tác vận chuyển", htmlContent);

            log.info("✅ Shipping partner unlocked email sent to: {}", partnerEmail);
        } catch (Exception e) {
            log.error("❌ Failed to send shipping partner unlocked email to {}: {}", partnerEmail, e.getMessage(), e);
        }
    }

    @Override
    @Async
    public void sendWelcomeEmail(String userEmail, String userName) {
        try {
            Context context = new Context();
            context.setVariable("userName", userName);
            context.setVariable("appName", appName);
            context.setVariable("appUrl", appUrl);
            context.setVariable("supportEmail", supportEmail);

            String htmlContent = templateEngine.process("emails/welcome", context);
            sendHtmlEmail(userEmail, "🎉 Chào mừng bạn đến với " + appName, htmlContent);

            log.info("✅ Welcome email sent to: {}", userEmail);
        } catch (Exception e) {
            log.error("❌ Failed to send welcome email to {}: {}", userEmail, e.getMessage(), e);
        }
    }

    @Override
    @Async
    public void sendPasswordResetEmail(String userEmail, String userName, String resetToken) {
        try {
            Context context = new Context();
            context.setVariable("userName", userName);
            context.setVariable("resetLink", appUrl + "/reset-password?token=" + resetToken);
            context.setVariable("appName", appName);
            context.setVariable("supportEmail", supportEmail);

            String htmlContent = templateEngine.process("emails/password-reset", context);
            sendHtmlEmail(userEmail, "🔐 Yêu cầu đặt lại mật khẩu", htmlContent);

            log.info("✅ Password reset email sent to: {}", userEmail);
        } catch (Exception e) {
            log.error("❌ Failed to send password reset email to {}: {}", userEmail, e.getMessage(), e);
        }
    }

    @Override
    @Async
    public void sendOrderConfirmationEmail(String userEmail, String userName, String orderDetails) {
        try {
            Context context = new Context();
            context.setVariable("userName", userName);
            context.setVariable("orderDetails", orderDetails);
            context.setVariable("appName", appName);
            context.setVariable("appUrl", appUrl);
            context.setVariable("supportEmail", supportEmail);
            context.setVariable("currentDate", getCurrentDate());

            String htmlContent = templateEngine.process("emails/order-confirmation", context);
            sendHtmlEmail(userEmail, "📦 Xác nhận đơn hàng từ " + appName, htmlContent);

            log.info("✅ Order confirmation email sent to: {}", userEmail);
        } catch (Exception e) {
            log.error("❌ Failed to send order confirmation email to {}: {}", userEmail, e.getMessage(), e);
        }
    }

    @Override
    @Async
    public void sendOrderStatusUpdateEmail(String userEmail, String userName, String orderStatus, String orderDetails) {
        try {
            Context context = new Context();
            context.setVariable("userName", userName);
            context.setVariable("orderStatus", orderStatus);
            context.setVariable("orderDetails", orderDetails);
            context.setVariable("appName", appName);
            context.setVariable("appUrl", appUrl);
            context.setVariable("supportEmail", supportEmail);
            context.setVariable("currentDate", getCurrentDate());

            String htmlContent = templateEngine.process("emails/order-status-update", context);
            sendHtmlEmail(userEmail, "🔮 Cập nhật trạng thái đơn hàng", htmlContent);

            log.info("✅ Order status update email sent to: {}", userEmail);
        } catch (Exception e) {
            log.error("❌ Failed to send order status update email to {}: {}", userEmail, e.getMessage(), e);
        }
    }

    // ==================== PRIVATE HELPER METHODS ====================

    private void sendHtmlEmail(String to, String subject, String htmlContent) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setFrom(fromEmail);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(htmlContent, true);

        mailSender.send(message);
    }

    private void sendSimpleEmail(String to, String subject, String text) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(to);
            message.setSubject(subject);
            message.setText(text);

            mailSender.send(message);
        } catch (MailException e) {
            log.error("❌ Failed to send simple email to {}: {}", to, e.getMessage(), e);
        }
    }

    private Context createMerchantContext(String email, String name, String restaurantName, String reason) {
        Context context = new Context();
        context.setVariable("merchantName", name);
        context.setVariable("merchantEmail", email);
        context.setVariable("restaurantName", restaurantName);
        context.setVariable("appName", appName);
        context.setVariable("appUrl", appUrl);
        context.setVariable("supportEmail", supportEmail);
        context.setVariable("currentDate", getCurrentDate());
        return context;
    }

    private String getCurrentDate() {
        return LocalDateTime.now().format(DATE_FORMATTER);
    }
}