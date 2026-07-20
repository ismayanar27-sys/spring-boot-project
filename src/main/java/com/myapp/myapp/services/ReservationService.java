package com.myapp.myapp.services;

import com.myapp.myapp.models.Reservation;

public interface ReservationService {

    /* Reservation artıq yalnız bazaya yazılmır.
     * Tarix + saat və restaurant capacity yoxlamaları
     * Service qatında aparılır.
     *
     * Buna görə əməliyyatın uğurlu olub-olmadığını
     * Controller-ə qaytarmaq üçün boolean istifadə olunur.
     */

    // Rezervasiyanı validation və capacity yoxlamasından keçirərək yadda saxlayır.
    boolean saveReservation(Reservation reservation);

    // YENİ METOD: Aktiv rezervasiyaların ümumi sayını gətirir (Projects sayğacı üçün)
    long countReservations();
}