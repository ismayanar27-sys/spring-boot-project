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
import java.util.List;

@Service
@Slf4j
public class ReservationServiceImpl implements ReservationService {

    private final ReservationRepository reservationRepository;
    private final EmailService emailService;

    // application.properties faylından adminin e-poçt ünvanını oxuyur
    @Value("${admin.email.recipient:ismayanar27@gmail.com}")
    private String adminEmailRecipient;

    // Restaurantın maksimum tutumu.
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
        // Eyni tarix və saat üzrə aktiv reservation-lar database-dən gətirilir.
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
        Reservation savedReservation = reservationRepository.save(reservation);

        // Rezervasiya bazaya yazıldıqdan sonra adminə bildiriş göndəririk
        sendAdminNotification(savedReservation);

        return true;
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
                            "<b>Status:</b> %s<br>" +
                            "<h3>Qeyd:</h3>%s",

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
     * Bazadakı bütün aktiv (CANCELLED olmayan) rezervasiyaların sayını qaytarır.
     *
     * DÜZƏLDİLDİ: Əvvəlki versiyada `findAll()` ilə BÜTÜN cədvəl yaddaşa
     * çəkilib, sonra Java tərəfində `.stream().filter(...).count()` ilə
     * sayılırdı. Bu, yalnız bir ədəd lazım olan yerdə bütün sətirləri
     * (adları, telefonları, mesajları və s.) lüzumsuz yerə bazadan çəkir.
     *
     * SİLİNDİ: reservationRepository.findAll().stream().filter(r -> r.getStatus() != ReservationStatus.CANCELLED).count()
     *
     * ƏVƏZİNƏ: `ReservationRepository.countByStatusNot(...)` istifadə olunur -
     * bu, bazada birbaşa "SELECT COUNT(*) ... WHERE status <> ?" işlədir.
     */
    @Override
    @Transactional(readOnly = true)
    public long countReservations() {

        return reservationRepository.countByStatusNot(ReservationStatus.CANCELLED);
    }

    /**
     * Admin panel üçün bütün rezervasiyaları qaytarır.
     *
     * DÜZƏLDİLDİ: `findAll()` (sıralanmamış) əvəzinə `findAllByOrderByCreatedAtDesc()`
     * istifadə olunur ki, admin panelində ən yeni rezervasiyalar yuxarıda görünsün.
     *
     * SİLİNDİ: reservationRepository.findAll();
     */
    @Override
    @Transactional(readOnly = true)
    public List<Reservation> getAllReservations() {

        return reservationRepository.findAllByOrderByCreatedAtDesc();
    }

    /**
     * ID-yə görə rezervasiya gətirir.
     */
    @Override
    @Transactional(readOnly = true)
    public Reservation getReservationById(Long id) {

        return reservationRepository.findById(id)
                .orElse(null);
    }

    /**
     * Statusa görə rezervasiyaları filter edir.
     *
     * DÜZƏLDİLDİ: Əvvəlki versiyada `findAll()` ilə BÜTÜN cədvəl yaddaşa
     * çəkilib, sonra Java tərəfində `.stream().filter(...)` ilə statusa
     * görə süzülürdü - filtrasiya bazanın WHERE şərti (indeksli sütun
     * üzərində) əvəzinə tətbiqin özündə, indekssiz şəkildə aparılırdı.
     *
     * SİLİNDİ: reservationRepository.findAll().stream().filter(r -> r.getStatus() == status).toList()
     *
     * ƏVƏZİNƏ: artıq mövcud olan `ReservationRepository.findByStatus(status)`
     * metodu istifadə olunur - filtrasiya birbaşa SQL-in WHERE şərti ilə,
     * bazanın özündə aparılır.
     */
    @Override
    @Transactional(readOnly = true)
    public List<Reservation> getReservationsByStatus(ReservationStatus status) {

        return reservationRepository.findByStatus(status);
    }

    /**
     * Admin paneldən reservation statusunu dəyişir.
     */
    @Override
    @Transactional
    public boolean updateReservationStatus(
            Long id,
            ReservationStatus status
    ) {

        Reservation reservation = reservationRepository.findById(id)
                .orElse(null);

        if (reservation == null) {
            return false;
        }

        reservation.setStatus(status);

        reservationRepository.save(reservation);

        log.info(
                "Reservation status yeniləndi. ID={}, Status={}",
                id,
                status
        );

        return true;
    }

    @Override
    public Object countReservationsByStatus(ReservationStatus reservationStatus) {
        return null;
    }
}