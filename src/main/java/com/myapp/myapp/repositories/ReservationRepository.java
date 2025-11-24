package com.myapp.myapp.repositories;

import com.myapp.myapp.models.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    // Spring Data JPA bizə bazada məlumatı saxlamaq, tapmaq və silmək üçün
    // bütün metodları (məsələn: save(), findAll()) avtomatik yaradır.
}