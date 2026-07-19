package com.myapp.myapp.services.impl;

import com.myapp.myapp.models.Reservation;
import com.myapp.myapp.repositories.ReservationRepository;
import com.myapp.myapp.services.EmailService;
import com.myapp.myapp.services.ReservationService;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class ReservationServiceImpl implements ReservationService {

    private final ReservationRepository reservationRepository;
    private final EmailService emailService;

    // application.properties faylından adminin e-poçt ünvanını oxuyur
    @Value("${admin.email.recipient:ismayanar27@gmail.com}")
    private String adminEmailRecipient;

    // Konstruktor vasitəsilə asılılıqların inyeksiyası
    public ReservationServiceImpl(
            ReservationRepository reservationRepository,
            EmailService emailService
    ) {
        this.reservationRepository = reservationRepository;
        this.emailService = emailService;
    }

    @Override
    @Transactional
    public void saveReservation(Reservation reservation) {

        // Rezervasiyanı bazaya yazırıq
        Reservation savedReservation =
                reservationRepository.save(reservation);

        // Rezervasiya bazaya yazıldıqdan sonra adminə bildiriş göndəririk
        sendAdminNotification(savedReservation);
    }

    private void sendAdminNotification(Reservation reservation) {

        try {
            final String subject =
                    "Yeni rezervasiya: " + reservation.getName();

            final String body = String.format(
                    "<b>Ad:</b> %s<br>" +
                            "<b>Telefon:</b> %s<br>" +
                            "<b>E-poçt:</b> %s<br>" +
                            "<b>Tarix:</b> %s<br>" +
                            "<b>Saat:</b> %s<br>" +
                            "<b>Nəfər sayı:</b> %s<br>" +
                            "<h3>Qeyd:</h3>%s",

                    reservation.getName(),
                    reservation.getPhone(),
                    reservation.getEmail(),
                    reservation.getReservationDate(),
                    reservation.getReservationTime(),
                    reservation.getPeople(),
                    reservation.getMessage()
            );

            emailService.sendEmail(
                    adminEmailRecipient,
                    subject,
                    body
            );

        } catch (Exception e) {

            // E-poçt göndərilməsə belə, rezervasiya bazada qalır
            log.error(
                    "Adminə rezervasiya e-poçtu göndərilərkən xəta baş verdi",
                    e
            );
        }
    }

    /**
     * Bazadakı bütün rezervasiyaların sayını qaytarır.
     */
    @Override
    @Transactional(readOnly = true)
    public long countReservations() {
        return reservationRepository.count();
    }

}