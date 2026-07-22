package com.myapp.myapp.services;

import com.fasterxml.jackson.databind.ObjectMapper; // JSON yaratmaq üçün (Jackson artıq layihədə var, spring-boot-starter-web ilə gəlir)
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.slf4j.Slf4j; // Loglama üçün (System.out/err əvəzinə)
import org.springframework.beans.factory.annotation.Value; // application.properties-dən dəyər oxumaq üçün
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient; // Java-nın öz daxili HTTP client-i - əlavə kitabxana lazım deyil
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

/**
 * Mail göndərmə xidməti.
 *
 * ƏVVƏLKİ VERSİYA (SMTP/Gmail vasitəsilə) burada idi, amma dəyişdirildi, çünki:
 * Render.com kimi bəzi pulsuz hosting-lər SMTP portlarına (25, 465, 587) çıxışı
 * bloklayır - buna görə Gmail-ə mail göndərmə cəhdi "Connect timed out" xətası verirdi.
 *
 * YENİ VERSİYA: Brevo (əvvəlki adı Sendinblue) adlı email provayderinin HTTP API-sini
 * istifadə edir. HTTP (443 portu) bloklanmır, ona görə bu, problemi tam həll edir.
 */
@Service
@Slf4j // Bu annotasiya avtomatik "log" adlı bir obyekt yaradır (log.info, log.error və LICENSE. üçün)
public class EmailService {

    // Brevo-nun mail göndərmə üçün rəsmi API ünvanı (dəyişmir, sabitdir)
    private static final String BREVO_API_URL = "https://api.brevo.com/v3/smtp/email";

    // HTTP sorğuları göndərmək üçün alət. 10 saniyə connect timeout qoyulub ki,
    // əgər şəbəkə problemi olsa, tətbiq əbədi gözləməsin.
    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    // Java obyektlərini JSON mətninə çevirmək üçün (Brevo-ya JSON formatında sorğu göndərməliyik)
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${brevo.api-key}")
    private String brevoApiKey;

    // Brevo-da "Verified" statusda olan göndərən email ünvanı (sənin ismayanar27@gmail.com-un)
    @Value("${brevo.sender-email:ismayanar27@gmail.com}")
    private String senderEmail;

    // Mail alanların "Kimdən" sahəsində görəcəyi ad (sadəcə görünüş üçündür, funksionallığa təsir etmir)
    @Value("${brevo.sender-name:Yummy Restaurant}")
    private String senderName;

    /**
     * Hər növ e-poçtu HTML formatında göndərmək üçün ümumi metod.
     * (Metodun adı və parametrləri əvvəlki versiya ilə eynidir - ona görə
     * ContactServiceImpl, ReservationServiceImpl kimi bu metodu çağıran yerlərdə
     * heç bir dəyişiklik lazım deyil.)
     */
    public void sendEmail(String toEmail, String subject, String htmlBody) {
        try {
            // 1. ADDIM: Brevo-nun tələb etdiyi JSON strukturunu addım-addım qururuq.
            // Brevo belə bir JSON gözləyir:
            // { "sender": {...}, "to": [{...}], "subject": "...", "htmlContent": "..." }
            ObjectNode payload = objectMapper.createObjectNode();

            // Göndərən məlumatı (ad + email)
            ObjectNode sender = objectMapper.createObjectNode();
            sender.put("name", senderName);
            sender.put("email", senderEmail);
            payload.set("sender", sender);

            // Alıcı məlumatı (Brevo-nun API-si "to" sahəsini massiv/array kimi tələb edir,
            // ona görə tək alıcı olsa belə, array içinə qoyuruq)
            ObjectNode recipient = objectMapper.createObjectNode();
            recipient.put("email", toEmail);
            payload.putArray("to").add(recipient);

            payload.put("subject", subject);
            payload.put("htmlContent", htmlBody); // HTML formatlı mətn (əvvəlki setText(body, true)-un qarşılığı)

            // JSON obyektini mətnə (String) çeviririk - HTTP sorğusunda bunu göndərəcəyik
            String requestBody = objectMapper.writeValueAsString(payload);

            // 2. ADDIM: HTTP POST sorğusunu qururuq
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BREVO_API_URL))
                    .header("accept", "application/json")
                    .header("content-type", "application/json")
                    .header("api-key", brevoApiKey) // Brevo bu başlıqla kimin göndərdiyini tanıyır
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .timeout(Duration.ofSeconds(10))
                    .build();

            // 3. ADDIM: Sorğunu göndəririk və cavabı gözləyirik
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            // 4. ADDIM: Cavabın statusuna görə uğurlu/uğursuz olduğunu yoxlayırıq
            // (200-299 arası status kodları HTTP-də "uğurlu" mənasını verir)
            if (response.statusCode() >= 200 && response.statusCode() < 300) {
                log.info("E-poçt Brevo vasitəsilə uğurla göndərildi: {}", toEmail);
            } else {
                log.error("Brevo e-poçt göndərmə xətası. Status: {}, Cavab: {}", response.statusCode(), response.body());
            }
        } catch (Exception e) {
            // Mail göndərilməsi uğursuz olsa da, bu, çağıran metodun (məsələn rezervasiya
            // yadda saxlama) uğurunu təsirləndirmir - çağıran tərəfdə artıq öz try-catch-i var.
            log.error("E-poçt göndərilməsi zamanı gözlənilməyən xəta baş verdi", e);
        }
    }
    public void sendOrderConfirmationEmail(String toEmail, String subject, String body) {
        sendEmail(toEmail, subject, body);
    }
}