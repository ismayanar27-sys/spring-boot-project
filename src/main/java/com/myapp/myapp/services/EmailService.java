package com.myapp.myapp.services;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.internet.MimeMessage; // MimeMessage üçün vacib import
import jakarta.mail.MessagingException;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    /**
     * Hər növ e-poçtu HTML formatında göndərmək üçün ümumi metod.
     */
    public void sendEmail(String toEmail, String subject, String body) {
        try {
            // SimpleMailMessage əvəzinə MimeMessage istifadə edirik
            MimeMessage message = mailSender.createMimeMessage();

            // true - HTML tərkibə icazə verir, "UTF-8" - Azərbaycan hərfləri üçün
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom("ismayanar27@gmail.com");

            helper.setTo(toEmail);
            helper.setSubject(subject);

            // **VACİB DƏYİŞİKLİK:** true flag-ı mətni HTML kimi qəbul edir.
            helper.setText(body, true);

            mailSender.send(message);
            System.out.println("E-poçt uğurla HTML formatında göndərildi: " + toEmail);
        } catch (MailException e) {
            System.err.println("XƏTA: E-poçt göndərilməsi zamanı xəta baş verdi. Zəhmət olmasa, App Password-u yoxlayın.");
            e.printStackTrace();
        } catch (MessagingException e) {
            System.err.println("XƏTA: MimeMessage yaratılması xətası: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Əvvəlki sifariş təsdiqi metodu (sendEmail metodunu istifadə edir)
    public void sendOrderConfirmationEmail(String toEmail, String subject, String body) {
        sendEmail(toEmail, subject, body);
    }
}
