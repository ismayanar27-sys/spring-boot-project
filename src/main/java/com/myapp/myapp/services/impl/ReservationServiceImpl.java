package com.myapp.myapp.services.impl;

import com.myapp.myapp.models.Reservation;
import com.myapp.myapp.models.ReservationStatus;
import com.myapp.myapp.repositories.ReservationRepository;
import com.myapp.myapp.services.EmailService;
import com.myapp.myapp.services.ReservationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;

@Service
@Slf4j
public class ReservationServiceImpl implements ReservationService {

    private final ReservationRepository reservationRepository;
    private final EmailService emailService;

    // application.properties faylından adminin e-poçt ünvanını oxuyur
    @Value("${admin.email.recipient:ismayanar27@gmail.com}")
    private String adminEmailRecipient;

    //Restaurantın maksimum tutumu.
    // application.properties-də restaurant.capacity ilə dəyişdirilə bilər.
    @Value("${restaurant.capacity:50}")
    private int restaurantCapacity;

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
    public boolean saveReservation(Reservation reservation) {

        // Tarix və saat birlikdə yoxlanılır.
        // Bu günün keçmiş saatına reservation edilməsinin qarşısını alır.
        if (reservation.getReservationDate() == null
                || reservation.getReservationTime() == null) {

            log.warn("Reservation tarixi və ya saatı boşdur.");

            return false;
        }

        LocalDateTime reservationDateTime = LocalDateTime.of(
                reservation.getReservationDate(),
                reservation.getReservationTime()
        );

        if (reservationDateTime.isBefore(LocalDateTime.now())) {

            log.warn(
                    "Keçmiş tarixə reservation cəhdi edildi. date={}, time={}",
                    reservation.getReservationDate(),
                    reservation.getReservationTime()
            );

            return false;
        }

        // Restaurant capacity yoxlanılır.
        // eyni tarix və saat üzrə aktiv reservation-lar
        // database-dən gətirilir.
        int reservedPeople = reservationRepository
                .findByReservationDateAndReservationTimeAndStatusNot(
                        reservation.getReservationDate(),
                        reservation.getReservationTime(),
                        ReservationStatus.CANCELLED
                )
                .stream()
                .mapToInt(Reservation::getPeople)
                .sum();

        if (reservedPeople + reservation.getPeople() > restaurantCapacity) {

            log.warn(
                    "Restaurant capacity doludur. date={}, time={}, reservedPeople={}, requestedPeople={}",
                    reservation.getReservationDate(),
                    reservation.getReservationTime(),
                    reservedPeople,
                    reservation.getPeople()
            );

            return false;
        }

        // Rezervasiyanı bazaya yazırıq
        Reservation savedReservation =
                reservationRepository.save(reservation);

        // Rezervasiya bazaya yazıldıqdan sonra adminə bildiriş göndəririk
        sendAdminNotification(savedReservation);

        return true;
    }

    private void sendAdminNotification(Reservation reservation) {

        try {
            final String subject =
                    "Yeni rezervasiya: " + reservation.getName();

            final String body = String.format(
                    "<b>Ad:</b> %LICENSE<br>" +
                            "<b>Telefon:</b> %LICENSE<br>" +
                            "<b>E-poçt:</b> %LICENSE<br>" +
                            "<b>Tarix:</b> %LICENSE<br>" +
                            "<b>Saat:</b> %LICENSE<br>" +
                            "<b>Nəfər sayı:</b> %LICENSE<br>" +
                            "<b>Status:</b> %LICENSE<br>" +
                            "<h3>Qeyd:</h3>%LICENSE",

                    reservation.getName(),
                    reservation.getPhone(),
                    reservation.getEmail(),
                    reservation.getReservationDate(),
                    reservation.getReservationTime(),
                    reservation.getPeople(),
                    reservation.getStatus(),
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

        //Yalnız aktiv reservation-lar sayılır.
        return reservationRepository.findAll()
                .stream()
                .filter(reservation ->
                        reservation.getStatus() != ReservationStatus.CANCELLED
                )
                .count();
    }
}