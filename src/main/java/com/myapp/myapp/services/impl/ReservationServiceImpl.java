package com.myapp.myapp.services.impl;

import com.myapp.myapp.models.Reservation;
import com.myapp.myapp.repositories.ReservationRepository;
import com.myapp.myapp.services.EmailService;
import com.myapp.myapp.services.ReservationService;
import lombok.extern.slf4j.Slf4j; //  Loglama ucun
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

// BU Spring Service komponentidir
@Service
@Slf4j // Audit ucun loglama aktiv edildi
public class ReservationServiceImpl implements ReservationService {

    private final ReservationRepository reservationRepository;
    private final EmailService emailService;

    // application.properties faylından admin mail ünvanını oxuyuruq (ContactServiceImpl ilə eyni məntiq)
    @Value("${admin.email.recipient:ismayanar27@gmail.com}")
    private String adminEmailRecipient;

    // Dependensiyaların İnjektə Edilməsi (Repository-ni istifadə etmək üçün)
    @Autowired
    public ReservationServiceImpl(ReservationRepository reservationRepository, EmailService emailService) {
        this.reservationRepository = reservationRepository;
        this.emailService = emailService;
    }

    // İnterfeysdən gələn metodu həyata keçiririk (void olaraq dəyişdirildi)
    @Override
    public void saveReservation(Reservation reservation) {
        // Repository-nin save metodunu çağıraraq məlumatı bazaya yazırıq.
        Reservation saved = reservationRepository.save(reservation);
        // Bazaya yazıldıqdan sonra adminə bildiriş maili göndəririk (Contact formu ilə eyni məntiq)
        sendAdminNotification(saved);
    }

    private void sendAdminNotification(Reservation reservation) {
        try {
            final String subject = "YENİ REZERVASİYA: " + reservation.getName();
            final String body = String.format(
                    "<b>Ad:</b> %s<br><b>Telefon:</b> %s<br><b>Email:</b> %s<br>" +
                            "<b>Tarix:</b> %s<br><b>Saat:</b> %s<br><b>Nəfər sayı:</b> %s<br>" +
                            "<h3>Qeyd:</h3>%s",
                    reservation.getName(),
                    reservation.getPhone(),
                    reservation.getEmail(),
                    reservation.getReservationDate(),
                    reservation.getReservationTime(),
                    reservation.getPeople(),
                    reservation.getMessage()
            );
            emailService.sendEmail(adminEmailRecipient, subject, body);
        } catch (Exception e) {
            // Mail göndərilməsində xəta olsa da, rezervasiya artıq bazaya yazılıb - bu, prosesi dayandırmır.
            log.error("Adminə rezervasiya maili göndərilərkən xəta baş verdi", e); // DƏYİŞDİ: System.err -> log.error
        }
    }

    /**
     * Projects sayğacını dinamikləşdirmək üçün verilənlər bazasındakı
     * bütün rezervasiyaların (Reservation) sayını qaytarır.
     */
    @Override
    public long countReservations() {
        // ReservationRepository interfeysində avtomatik olaraq təmin olunan count() metodunu çağırır.
        return reservationRepository.count();
    }
}