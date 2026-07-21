package com.myapp.myapp.repositories;

import com.myapp.myapp.models.Reservation;
import com.myapp.myapp.models.ReservationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    // Spring Data JPA bizə bazada məlumatı saxlamaq, tapmaq və silmək üçün
    // bütün metodları (məsələn: save(), findAll()) avtomatik yaradır.

    // Eyni tarix və saat üzrə yalnız aktiv reservation-ları gətirir.
    // CANCELLED reservation-lar capacity hesablamasına daxil edilmir.
    List<Reservation> findByReservationDateAndReservationTimeAndStatusNot(
            LocalDate reservationDate,
            LocalTime reservationTime,
            ReservationStatus status
    );

    // Admin panelində statusa görə rezervasiyaları filtrləmək üçün.
    List<Reservation> findByStatus(ReservationStatus status);

    // Admin panelində ən yeni rezervasiyaları yuxarıda göstərmək üçün.
    List<Reservation> findAllByOrderByCreatedAtDesc();
}