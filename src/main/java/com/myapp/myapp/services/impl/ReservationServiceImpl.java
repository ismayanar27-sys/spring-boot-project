package com.myapp.myapp.services.impl;

import com.myapp.myapp.models.Reservation;
import com.myapp.myapp.repositories.ReservationRepository;
import com.myapp.myapp.services.ReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

// BU Spring Service komponentidir
@Service
public class ReservationServiceImpl implements ReservationService {

    private final ReservationRepository reservationRepository;

    // Dependensiyaların İnjektə Edilməsi (Repository-ni istifadə etmək üçün)
    @Autowired
    public ReservationServiceImpl(ReservationRepository reservationRepository) {
        this.reservationRepository = reservationRepository;
    }

    // İnterfeysdən gələn metodu həyata keçiririk (void olaraq dəyişdirildi)
    @Override
    public void saveReservation(Reservation reservation) {
        // Repository-nin save metodunu çağıraraq məlumatı bazaya yazırıq.
        // Geriyə heç nə qaytarmağa (return) ehtiyac yoxdur.
        reservationRepository.save(reservation);
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