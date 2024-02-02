package com.example.movieapp.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class MailService {
    private final JavaMailSender javaMailSender;
    private final TemplateEngine templateEngine;

    // Send mail confirm registration
    public void sendMailConfirmRegistration(Map<String, String> data) {
        log.info("sendMailConfirmRegistration");
        log.info("Sending email request : {}", data);
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(data.get("email"));
            helper.setSubject("Xác nhận đăng ký tài khoản");

            // Create the Thymeleaf context
            Context context = new Context();
            context.setVariable("username", data.get("username"));
            context.setVariable("token", data.get("token"));

            // Use the template engine to process the template
            String htmlContent = templateEngine.process("web/mail-template/confirmation-account", context);
            helper.setText(htmlContent, true); // Enable HTML content

            javaMailSender.send(message);
        } catch (MessagingException e) {
            log.error("Error when sending email: " + e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    public void sendMailResetPassword(Map<String, String> data) {
        log.info("sendMailResetPassword");
        log.info("Sending email request : {}", data);
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(data.get("email"));
            helper.setSubject("Xác nhận đặt lại mật khẩu");

            // Create the Thymeleaf context
            Context context = new Context();
            context.setVariable("username", data.get("username"));
            context.setVariable("token", data.get("token"));

            // Use the template engine to process the template
            String htmlContent = templateEngine.process("web/mail-template/reset-password", context);
            helper.setText(htmlContent, true); // Enable HTML content

            javaMailSender.send(message);
        } catch (MessagingException e) {
            log.error("Error when sending email: " + e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }
}
