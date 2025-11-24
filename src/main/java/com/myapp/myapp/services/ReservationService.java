package com.myapp.myapp.services;

import com.myapp.myapp.models.Reservation;

public interface ReservationService {

    // Artıq geriyə obyekti qaytarmağa ehtiyac yoxdur.
    // Sadəcə məlumatın bazaya yazılması kifayətdir.
    void saveReservation(Reservation reservation);

    // YENİ METOD: Rezervasiyaların ümumi sayını gətirir (Projects sayğacı üçün)
    long countReservations();
}